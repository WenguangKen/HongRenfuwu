package com.athlunakms.ai.service;

import com.athlunakms.ai.dto.mail.MailIntentAnalyzeRequest;
import com.athlunakms.ai.dto.mail.MailIntentAnalyzeResponse;
import com.athlunakms.ai.dto.mail.MailPersonalizeRequest;
import com.athlunakms.ai.dto.mail.MailPersonalizeResponse;
import com.athlunakms.ai.dto.mail.MailTemplateAiRequest;
import com.athlunakms.ai.dto.mail.MailTemplateRefineRequest;
import com.athlunakms.ai.dto.mail.MailTemplateAiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailAiService {

    private static final Pattern JSON_BLOCK = Pattern.compile("\\{[\\s\\S]*}");

    private final ChatLanguageModel chatLanguageModel;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${services.backend-influencer.url}")
    private String influencerServiceUrl;

    public MailTemplateAiResponse generateTemplate(MailTemplateAiRequest req) {
        String brief = buildBrief(req);
        boolean hasProducts = req.getSampleProducts() != null && !req.getSampleProducts().isEmpty();
        String purposeDirective = purposeDirective(req.getPurpose());
        String styleDirective = visualStyleDirective(req.getVisualStyle());
        String prompt = """
                你是跨境 KOL 商务邮件专家。根据运营需求生成**视觉专业、像精品品牌邮件、又像真人 BD 写的**邮件正文 HTML（仅内层内容），避免 AI/公关通稿感。

                === 本封邮件的场景 / 用途（务必紧扣此场景写作）===
                %s

                === 输出要求 ===
                1. 主题与正文使用 %s 为主（红人多为该语言），专业、简洁、友好。
                2. 保留变量占位符：{{displayName}}、{{handle}}、{{realName}}、{{platform}}（platform 发信时按红人社媒平台自动填入，勿写死单一平台）。
                3. 输出必须是纯 JSON，不要 markdown，格式：
                {"subjectTpl":"...","htmlBody":"...","textBody":"...","suggestedVariables":["displayName","handle"],"notesZh":"中文说明生成思路"}

                === 重要：htmlBody 只写「内层正文」，不要写 <html>/<head>/<body>，系统会自动套上精美外框（渐变顶栏、圆角卡片、页脚）===
                内层正文须包含以下视觉模块（table+inline-style，邮件客户端兼容）：
                - 开头称呼段：Hi {{displayName}}，
                - 1 个「亮点信息框」：用 table 做圆角浅紫/浅蓝背景框，放佣金率或核心合作条件（带 emoji 点缀如 ✨ 💰 均可，最多 2 个）
                - 正文 2~4 段 prose（人味，见下方规则）
                - 商品区占位符 %s（一字不漏，系统替换成图文卡片：仅商品图+名称+SKU，**禁止显示价格**；勿自行手写 Featured products 列表）
                - 1 个 CTA 按钮：table 包裹的渐变圆角 <a>，文案如 "I'm in — reply here" / "有兴趣，回这封邮件"，href 用 mailto:；**按钮文字不得为空**
                - 签名段（自然口吻，非 Best regards 套话）

                === 禁止 ===
                - JavaScript、<script>、onload/onclick 等事件、追踪像素
                - 外链样式表（仅 inline style；系统会在外层注入少量 @keyframes 动画）
                - 写死单一商品名（商品交给占位符）

                === 运营需求 ===
                %s
                %s

                %s

                %s
                %s
                """.formatted(
                purposeDirective,
                langHint(req.getLanguage()),
                PRODUCT_PLACEHOLDER,
                brief,
                StringUtils.hasText(req.getReferenceHtml())
                        ? "\n参考模版：\n" + req.getReferenceHtml()
                        : "",
                styleDirective,
                HUMAN_VOICE_RULES,
                EMAIL_DESIGN_RULES);

        String raw;
        try {
            raw = chatLanguageModel.generate(prompt);
        } catch (Exception e) {
            log.error("ChatLanguageModel.generate failed for template", e);
            throw new IllegalStateException(
                    "AI 模型调用失败：" + rootCauseMessage(e) + "。请检查 AI_DOUBAO_API_KEY 与模型 ID 是否已配置。", e);
        }

        MailTemplateAiResponse resp = parseJson(raw, MailTemplateAiResponse.class, () -> {
            MailTemplateAiResponse fallback = new MailTemplateAiResponse();
            fallback.setSubjectTpl("Collaboration opportunity - {{displayName}}");
            fallback.setHtmlBody("<p>Hi {{displayName}},</p><p>We would love to explore a collaboration.</p>");
            fallback.setTextBody("Hi {{displayName}}, We would love to explore a collaboration.");
            fallback.setSuggestedVariables(List.of("displayName", "handle", "realName"));
            fallback.setNotesZh("AI 解析失败，已返回默认模版");
            return fallback;
        });

        // 把占位符替换成真实的商品卡片 HTML（图 + 名 + SKU + 价），保证图片 URL 真实
        if (hasProducts) {
            String cards = renderProductCardsHtml(req.getSampleProducts());
            String html = resp.getHtmlBody() != null ? resp.getHtmlBody() : "";
            if (html.contains(PRODUCT_PLACEHOLDER)) {
                html = html.replace(PRODUCT_PLACEHOLDER, cards);
            } else {
                // AI 没听话没放占位符 —— 兜底插到 </div> 之前 / 签名之前
                int insertAt = html.lastIndexOf("</div>");
                if (insertAt < 0) insertAt = html.length();
                html = html.substring(0, insertAt) + cards + html.substring(insertAt);
            }
            resp.setHtmlBody(html);
            // text 版本附加商品文本清单
            String text = resp.getTextBody() != null ? resp.getTextBody() : "";
            resp.setTextBody(text + "\n\n" + renderProductListText(req.getSampleProducts()));
        } else {
            // 没商品也清一下兜底占位符，免得用户看到漏字
            if (resp.getHtmlBody() != null) {
                resp.setHtmlBody(resp.getHtmlBody().replace(PRODUCT_PLACEHOLDER, ""));
            }
        }
        if (resp.getHtmlBody() != null) {
            String html = sanitizeEmailHtml(resp.getHtmlBody());
            html = fixEmptyCtaButtons(html);
            resp.setHtmlBody(wrapProfessionalEmailShell(
                    html,
                    StringUtils.hasText(req.getBrand()) ? req.getBrand() : "Athluna",
                    req.getVisualStyle()));
        }
        return resp;
    }

    /**
     * 在已有模版基础上做视觉微调（颜色、排版、间距、CTA 等），不重写合作文案。
     */
    public MailTemplateAiResponse refineTemplate(MailTemplateRefineRequest req) {
        if (!StringUtils.hasText(req.getInstruction())) {
            throw new IllegalArgumentException("请说明要如何微调模版");
        }
        if (!StringUtils.hasText(req.getHtmlBody())) {
            throw new IllegalArgumentException("当前模版 HTML 为空，无法微调");
        }
        String styleHint = StringUtils.hasText(req.getVisualStyle())
                ? visualStyleDirective(req.getVisualStyle())
                : "";
        String prompt = """
                你是邮件 HTML 视觉微调专家。运营已对模版满意，只需按指令调整**颜色、排版、间距、字号、圆角、CTA 按钮样式、顶栏/页脚视觉**。

                === 硬性约束（必须遵守）===
                1. **保留全部变量占位符**：{{displayName}}、{{handle}}、{{platform}}、{{realName}} 等，拼写与数量不得变。
                2. **保留 data-athluna-shell 外层邮件壳**（若已有）；保留 Featured products 商品卡片整块 HTML（含 img、SKU），勿删商品图。
                3. **禁止**改写合作条款、佣金数字、品牌名、商品名的语义；正文句子尽量原样保留，仅允许标点级微调。
                4. **禁止** JavaScript、外链 CSS；只用 inline style；CTA 的 mailto 链接保留且按钮文字不得为空。
                5. **禁止**在商品卡片中显示价格。
                6. 若已有完整 HTML 壳，输出完整 htmlBody（含壳）；不要拆成只有内层片段。

                %s

                === 当前邮件主题 ===
                %s

                === 当前 HTML 正文 ===
                %s

                === 当前纯文本（可选参考）===
                %s

                === 运营微调指令 ===
                %s

                输出纯 JSON，不要 markdown：
                {"subjectTpl":"…","htmlBody":"…","textBody":"…","notesZh":"用中文 1~2 句说明做了哪些视觉调整"}
                """.formatted(
                styleHint,
                nullToEmpty(req.getSubjectTpl()),
                req.getHtmlBody().trim(),
                nullToEmpty(req.getTextBody()),
                req.getInstruction().trim());

        String raw;
        try {
            raw = chatLanguageModel.generate(prompt);
        } catch (Exception e) {
            log.error("ChatLanguageModel.generate failed for refine", e);
            throw new IllegalStateException(
                    "AI 微调失败：" + rootCauseMessage(e), e);
        }

        MailTemplateAiResponse resp = parseJson(raw, MailTemplateAiResponse.class, () -> {
            MailTemplateAiResponse fallback = new MailTemplateAiResponse();
            fallback.setSubjectTpl(req.getSubjectTpl());
            fallback.setHtmlBody(req.getHtmlBody());
            fallback.setTextBody(req.getTextBody());
            fallback.setNotesZh("AI 解析失败，模版未改动");
            return fallback;
        });

        if (!StringUtils.hasText(resp.getSubjectTpl())) {
            resp.setSubjectTpl(req.getSubjectTpl());
        }
        if (!StringUtils.hasText(resp.getHtmlBody())) {
            resp.setHtmlBody(req.getHtmlBody());
        }
        if (resp.getHtmlBody() != null) {
            String html = sanitizeEmailHtml(resp.getHtmlBody());
            html = fixEmptyCtaButtons(html);
            resp.setHtmlBody(html);
        }
        if (!StringUtils.hasText(resp.getTextBody()) && StringUtils.hasText(req.getTextBody())) {
            resp.setTextBody(req.getTextBody());
        }
        if (!StringUtils.hasText(resp.getNotesZh())) {
            resp.setNotesZh("已按您的要求调整模版视觉效果。");
        }
        return resp;
    }

    private static final String PRODUCT_PLACEHOLDER = "{{__PRODUCTS__}}";

    /** AI 内层正文的设计规范（外层壳由 wrapProfessionalEmailShell 自动添加） */
    private static final String EMAIL_DESIGN_RULES = """
            === 内层 HTML 视觉规范（table + inline style，专业邮件设计感）===
            - 段落：<p style="margin:0 0 16px 0;font-size:15px;line-height:1.65;color:#334155">
            - 亮点框示例（佣金/条件）：
              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="margin:20px 0"><tr><td style="background:linear-gradient(135deg,#eef2ff 0%%,#f5f3ff 100%%);border:1px solid #e0e7ff;border-radius:12px;padding:18px 20px">
              <div style="font-size:13px;font-weight:600;color:#6366f1;text-transform:uppercase;letter-spacing:0.06em;margin-bottom:8px">Partnership highlight</div>
              <div style="font-size:15px;color:#1e293b;line-height:1.55">…佣金/条件…</div></td></tr></table>
            - CTA 按钮示例（必须有可见文字 + 纯色 fallback background-color）：
              <table role="presentation" cellpadding="0" cellspacing="0" align="center" style="margin:28px auto"><tr><td align="center" style="border-radius:12px;background-color:#6366f1;background:linear-gradient(135deg,#6366f1 0%%,#7c3aed 100%%);box-shadow:0 6px 20px rgba(99,102,241,0.35)">
              <a href="mailto:" style="display:inline-block;padding:14px 36px;font-size:15px;font-weight:600;color:#ffffff;text-decoration:none;border-radius:12px">I'm interested — reply here</a></td></tr></table>
            - 强调词用 <strong style="color:#0f172a">，数字/佣金可加 color:#6366f1
            - 商品卡片由系统注入，禁止手写 Featured products 或重复 CTA
            """;

    /**
     * 全局「人味」写作约束：邀约、回复、模版生成共用，避免 AI/公关腔。
     */
    private static final String HUMAN_VOICE_RULES = """
            === 人味写作（必须遵守，像真人 BD 在跟创作者说话）===
            - 语气温暖、具体、有分寸：专业但不冷冰冰，别像系统通知或公关通稿。
            - 英文自然用缩略（I'm, we'd, you're, it's）；句式长短交替，避免每句同一结构。
            - 至少写一句「为什么找 TA」——点出 ta 的内容风格、品类或平台特点（从红人信息里取），让人感觉到被认真看过。
            - **禁用套话**：hope this email finds you well / delighted to reach out / touch base / synergy / leverage /
              I wanted to reach out / 诚挚邀请 / 此致敬礼 / 很高兴收到您的来信 / 我司荣幸地…
            - 用第一人称「I / 我」为主，少用「我方团队很高兴通知您」式公文腔。
            - 结尾自然即可（Cheers / Best / Talk soon / Looking forward to hearing from you / 期待你的回复），不必机械 Best regards。
            - 可带一点轻微口语（如 "Quick question" / "No pressure at all" / "Honestly love your…"），但不过度 emoji、不装熟。
            - 少用大段 bullet；合作条件用 2~3 句自然 prose 说清楚，必要时最多 3 个短要点。
            - 称呼用昵称或名字，像写给一个人，不是群发传单。
            """;

    /**
     * 合作意向分析：给运营看的 briefing 口吻（不是写给红人的邮件正文）。
     */
    private static final String INTENT_BRIEF_RULES = """
            === 给运营的口头 briefing（summaryZh / suggestedActionZh 专用）===
            - 读者是内部 BD 同事，像 Slack/微信里转述「这人回信说了啥、你接下来咋弄」。
            - summaryZh：1 句话，说清对方态度 + 具体诉求/卡点。用「TA/对方/红人昵称」作主语。
              好：「ketext 愿意合作，但要先看合同，确认后再寄样。」
              坏：「红人收到我方合作邀请后，明确表示乐意合作，并要求我方发送合作合同，待其确认后再安排样品合作。」
            - suggestedActionZh：1 句话，直接说「回邮件做什么」，不要以「建议：」开头，不要写「回复红人邮件」这种空指令。
              好：「把合同发过去，问有没有要改的条款，对方 OK 了再安排寄样。」
              坏：「建议：回复红人邮件，发送合作合同供其确认。」
            - 禁用报告体/公文腔：我方、贵方、明确表示、待其确认后再、供其确认、推动合作落地、及时跟进、确认具体细节。
            - 可以口语化：回他、把合同发过去、先别急着寄样、顺带问一句。
            """;

    /** 根据邮件场景生成给 AI 的"角色 + 信息结构 + 语气"指令 */
    private static String purposeDirective(String purpose) {
        String p = purpose == null ? "invite" : purpose.toLowerCase();
        return switch (p) {
            case "sample_followup" -> """
                    类型：**寄样跟进邮件**。前提：样品已寄出。
                    信息结构：问候 → 询问收货状况（包裹是否到达、产品是否完好）→ 询问内容计划（什么时候发、什么形式）→ 提供帮助（脚本建议/产品卖点/拍摄素材）→ 友好结尾。
                    语气：关心、专业、不催促；不要重复邀约话术，对方已经是合作伙伴。
                    """;
            case "commission_negotiate" -> """
                    类型：**佣金 / 条款协商邮件**。前提：红人对佣金或合作条款提出了异议或还价。
                    信息结构：理解并肯定对方诉求 → 说明我方报价的依据（行业基准/产品价位）→ 给出 2~3 档可选方案（佣金+寄样数量+长期分成）→ 邀请进一步沟通。
                    语气：尊重、灵活、双赢，避免硬碰硬。
                    """;
            case "content_brief" -> """
                    类型：**内容方向沟通邮件**。前提：合作已确认，准备进入内容生产环节。
                    信息结构：感谢确认合作 → 提供品牌价值点 / 必提关键词 / 禁用词 / 视频脚本要点 / 参考案例链接 → 截止时间 → 提供素材包 / 询问需求。
                    语气：清晰、结构化、像给创作者的简报（brief）。
                    """;
            case "post_publish_thanks" -> """
                    类型：**发布后感谢邮件**。前提：红人已经发布合作内容。
                    信息结构：表达由衷感谢（指出发布的具体内容/平台）→ 分享数据反馈或品牌团队的好评 → 邀请下次合作（新品/活动）→ 友好结尾。
                    语气：真诚、温暖、留下长期合作可能。
                    """;
            case "payment_settle" -> """
                    类型：**结算通知邮件**。前提：本期合作内容已完成，进入结算。
                    信息结构：致谢 → 本期佣金 / 销售数据明细 → 结算方式与到账时间 → 税务/发票提示 → 邀请下期合作。
                    语气：正式、清晰、数据准确；正文里写清金额、币种、结算周期。
                    """;
            case "reactivate" -> """
                    类型：**沉默红人激活邮件**。前提：之前发过邀约但未回信。
                    信息结构：换个开场（不要"上次邮件没收到回复"，要正向）→ 给一个新角度（新品、节日活动、新佣金方案）→ 用 1 个具体好处吸引（独家折扣码 / 限量样品）→ 低门槛 CTA（"回个 OK 我就寄样"）。
                    语气：轻松、不卑微、有新鲜感；不要让对方觉得被纠缠。
                    """;
            case "polite_decline" -> """
                    类型：**礼貌谢绝邮件**。前提：红人提出了我方做不到的要求（如过高佣金/独家协议/超出预算）。
                    信息结构：感谢对方对品牌的兴趣 → 坦诚说明本次无法满足某项要求的原因 → 保留未来合作可能（例如新一季活动/不同合作形式）→ 真诚祝福。
                    语气：尊重、温暖、给台阶；绝不傲慢，绝不冷漠。
                    """;
            case "general" -> """
                    类型：**通用商务沟通邮件**。
                    信息结构：根据运营需求灵活组织，但保留：问候 → 核心信息（1~2 段）→ 明确 CTA → 签名。
                    语气：专业、简洁、友好。
                    """;
            case "invite" -> """
                    类型：**首次合作邀约邮件**。前提：红人从未与品牌合作过。
                    信息结构：问候 → 自我介绍/品牌价值主张（1~2 句）→ 为什么选 TA（提到 ta 的平台或内容特点）→ 合作机会（寄样 + 佣金 + 内容形式）→ 低门槛 CTA（"如果感兴趣，回复一下就好"）→ 签名。
                    语气：像品牌 BD 认真看过账号后发的私信——真诚、具体、不硬推销；让红人感觉被尊重，不是群发。
                    """;
            default -> """
                    类型：**首次合作邀约邮件**。
                    信息结构：问候 → 自我介绍/价值主张 → 合作机会 → CTA → 签名。
                    语气：温暖、具体、像真人写的，不要模版感。
                    """;
        };
    }

    /** 用 inline-style 渲染商品图文卡片（邮件客户端最大兼容性：table+inline-style） */
    private static String renderProductCardsHtml(List<MailTemplateAiRequest.SampleProductItem> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" ")
          .append("style=\"margin:20px 0;border-collapse:separate;border-spacing:0 14px\">");
        sb.append("<tr><td style=\"font-size:13px;font-weight:600;color:#6366f1;text-transform:uppercase;letter-spacing:0.06em;padding-bottom:4px\">")
          .append("Featured products</td></tr>");
        for (MailTemplateAiRequest.SampleProductItem p : products) {
            String img = resolveProductImageUrl(p);
            String title = nullToEmpty(p.getTitle());
            String sku = nullToEmpty(p.getSku());
            sb.append("<tr><td style=\"background:#ffffff;border:1px solid #e2e8f0;border-left:4px solid #6366f1;"
                    + "border-radius:12px;padding:16px;vertical-align:top;"
                    + "box-shadow:0 2px 12px rgba(15,23,42,0.06)\">");
            sb.append("<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:100%\"><tr>");
            sb.append("<td style=\"width:96px;vertical-align:top;padding-right:16px\">");
            if (StringUtils.hasText(img)) {
                sb.append("<img src=\"").append(escapeAttr(img)).append("\" alt=\"")
                  .append(escapeAttr(title.isEmpty() ? "Product" : title)).append("\" ")
                  .append("width=\"92\" height=\"92\" ")
                  .append("style=\"width:92px;height:92px;object-fit:cover;border-radius:10px;display:block;"
                  + "border:1px solid #e2e8f0\"/>");
            } else {
                sb.append("<div style=\"width:92px;height:92px;border-radius:10px;background:#f1f5f9;"
                        + "border:1px solid #e2e8f0;display:block;text-align:center;line-height:92px;"
                        + "font-size:28px;color:#94a3b8\">📦</div>");
            }
            sb.append("</td>");
            sb.append("<td style=\"vertical-align:top\">")
              .append("<div style=\"font-size:16px;font-weight:600;color:#0f172a;margin-bottom:6px;line-height:1.35\">")
              .append(escapeHtml(title.isEmpty() ? "Sample Product" : title))
              .append("</div>");
            if (StringUtils.hasText(sku)) {
                sb.append("<div style=\"font-size:13px;color:#64748b\">SKU · ")
                  .append(escapeHtml(sku)).append("</div>");
            }
            sb.append("</td></tr></table></td></tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    /** 解析并规范化商品图 URL（兼容 image / mainImage 字段，Shopify CDN 加 width） */
    private static String resolveProductImageUrl(MailTemplateAiRequest.SampleProductItem p) {
        if (p == null) {
            return "";
        }
        String raw = nullToEmpty(p.getImageUrl());
        if (!StringUtils.hasText(raw)) {
            return "";
        }
        String url = raw.trim().replaceAll("^['\"\\[\\]]+|['\"\\[\\]]+$", "");
        if (url.startsWith("//")) {
            url = "https:" + url;
        }
        if (!url.startsWith("http")) {
            return "";
        }
        if (url.contains("cdn.shopify.com")) {
            int q = url.indexOf('?');
            String base = q >= 0 ? url.substring(0, q) : url;
            return base + "?width=240";
        }
        return url;
    }

    /**
     * 修复 AI 偶发生成的空 CTA 按钮（预览里显示为空白圆角条）。
     * 空白块 = 有 padding/圆角/shadow 的 mailto 链接但无可见文字。
     */
    private static String fixEmptyCtaButtons(String html) {
        if (!StringUtils.hasText(html)) {
            return html;
        }
        String out = html;
        // 空 mailto 链接 → 填入默认文案
        out = out.replaceAll(
                "(?is)(<a\\s[^>]*href=\"mailto:[^\"]*\"[^>]*>)\\s*(</a>)",
                "$1I'm interested — reply here$2");
        // 移除仍无文字的 CTA 外层 table（避免空白条）
        out = out.replaceAll(
                "(?is)<table[^>]*role=\"presentation\"[^>]*>\\s*<tr>\\s*<td[^>]*align=\"center\"[^>]*"
                        + "style=\"[^\"]*border-radius[^\"]*\"[^>]*>\\s*<a\\s[^>]*href=\"mailto:[^\"]*\"[^>]*>"
                        + "\\s*</a>\\s*</td>\\s*</tr>\\s*</table>",
                "");
        return out;
    }

    /** 去掉脚本/事件，邮件客户端本就不支持 JS */
    private static String sanitizeEmailHtml(String html) {
        if (html == null) return "";
        return html
                .replaceAll("(?is)<script[^>]*>.*?</script>", "")
                .replaceAll("(?i)\\son\\w+\\s*=\\s*\"[^\"]*\"", "")
                .replaceAll("(?i)\\son\\w+\\s*=\\s*'[^']*'", "");
    }

    private record EmailShellTheme(
            String outerBg,
            String headerStyle,
            String titleColor,
            String subtitleColor,
            String footerBg,
            String footerTextColor,
            String shimmerClass) {}

    private static EmailShellTheme resolveShellTheme(String visualStyle) {
        String s = visualStyle == null ? "gradient" : visualStyle.toLowerCase();
        return switch (s) {
            case "minimal" -> new EmailShellTheme(
                    "#f8fafc",
                    "background:#ffffff;border-bottom:2px solid #0f172a",
                    "#0f172a",
                    "#64748b",
                    "#ffffff",
                    "#94a3b8",
                    "");
            case "luxury" -> new EmailShellTheme(
                    "#0f0f0f",
                    "background:linear-gradient(135deg,#1c1917 0%,#292524 50%,#1c1917 100%);border-bottom:3px solid #c9a962",
                    "#fafaf9",
                    "rgba(201,169,98,0.92)",
                    "#1c1917",
                    "#a8a29e",
                    "");
            case "fresh" -> new EmailShellTheme(
                    "#ecfdf5",
                    "background:linear-gradient(135deg,#0d9488 0%,#06b6d4 55%,#22d3ee 100%);background-size:200% 200%;animation:athlunaShimmer 8s ease infinite",
                    "#ffffff",
                    "rgba(255,255,255,0.9)",
                    "#f0fdfa",
                    "#5eead4",
                    "athluna-header");
            case "warm" -> new EmailShellTheme(
                    "#fff7ed",
                    "background:linear-gradient(135deg,#ea580c 0%,#f97316 45%,#fb7185 100%);background-size:200% 200%;animation:athlunaShimmer 8s ease infinite",
                    "#ffffff",
                    "rgba(255,255,255,0.92)",
                    "#fffbeb",
                    "#fb923c",
                    "athluna-header");
            default -> new EmailShellTheme(
                    "#f1f5f9",
                    "background:linear-gradient(135deg,#6366f1 0%,#8b5cf6 45%,#a855f7 100%);background-size:200% 200%;animation:athlunaShimmer 8s ease infinite",
                    "#ffffff",
                    "rgba(255,255,255,0.88)",
                    "#f8fafc",
                    "#94a3b8",
                    "athluna-header");
        };
    }

    private static String visualStyleDirective(String visualStyle) {
        String s = visualStyle == null ? "gradient" : visualStyle.toLowerCase();
        return switch (s) {
            case "minimal" -> """
                    === 页面视觉风格：极简商务 ===
                    - 亮点框：白底 + 细灰边框，标题色 #0f172a，无渐变
                    - CTA：实心黑底 #0f172a 白字，圆角 8px，简洁
                    - 强调色 #0f172a，数字可用 #6366f1
                    """;
            case "luxury" -> """
                    === 页面视觉风格：轻奢黑金 ===
                    - 亮点框：深灰底 #292524 + 金色左边线 #c9a962
                    - CTA：金色渐变 #c9a962 → #d4af37，深字
                    - 强调色 #c9a962，整体克制、高级
                    """;
            case "fresh" -> """
                    === 页面视觉风格：清新活力 ===
                    - 亮点框：浅青绿底 #ecfdf5，边框 #99f6e4，标题色 #0d9488
                    - CTA：青绿渐变 #0d9488 → #06b6d4
                    - 强调色 #0d9488，适合运动/健康/户外品牌
                    """;
            case "warm" -> """
                    === 页面视觉风格：暖色亲和 ===
                    - 亮点框：暖橙浅底 #fff7ed，边框 #fed7aa，标题色 #ea580c
                    - CTA：珊瑚橙渐变 #ea580c → #fb7185
                    - 强调色 #f97316，适合美妆/生活方式
                    """;
            default -> """
                    === 页面视觉风格：品牌渐变（默认）===
                    - 亮点框：浅紫渐变 #eef2ff → #f5f3ff，标题色 #6366f1
                    - CTA：紫罗兰渐变 #6366f1 → #7c3aed
                    - 强调色 #6366f1
                    """;
        };
    }

    /**
     * 为 AI 生成的内层正文套上专业邮件外框：渐变顶栏、圆角白底卡片、淡入动画、页脚。
     * 邮件客户端不支持 JavaScript；@keyframes 在 Apple Mail / 部分客户端有效，其余优雅降级为静态样式。
     */
    private static String wrapProfessionalEmailShell(String innerHtml, String brand, String visualStyle) {
        if (!StringUtils.hasText(innerHtml)) {
            return innerHtml;
        }
        if (innerHtml.contains("data-athluna-shell=\"1\"")) {
            return innerHtml;
        }
        String inner = innerHtml.trim();
        String brandName = StringUtils.hasText(brand) ? escapeHtml(brand.trim()) : "Athluna";
        String teamLine = brandName + " · Creator Partnership";
        EmailShellTheme theme = resolveShellTheme(visualStyle);
        String headerClass = StringUtils.hasText(theme.shimmerClass())
                ? "athluna-header athluna-head-pad"
                : "athluna-head-pad";

        return """
                <style type="text/css">
                @keyframes athlunaFadeUp{from{opacity:0;transform:translateY(12px)}to{opacity:1;transform:translateY(0)}}
                @keyframes athlunaShimmer{0%%{background-position:0%% 50%%}50%%{background-position:100%% 50%%}100%%{background-position:0%% 50%%}}
                .athluna-fade{animation:athlunaFadeUp .6s ease-out both}
                .athluna-header{background-size:200%% 200%%;animation:athlunaShimmer 8s ease infinite}
                @media screen and (max-width:620px){.athluna-wrap{width:100%%!important;max-width:100%%!important}.athluna-pad{padding:22px 18px!important}.athluna-head-pad{padding:22px 18px!important}}
                </style>
                <table role="presentation" data-athluna-shell="1" data-visual-style="%s" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background:%s;margin:0;padding:0;font-family:'Helvetica Neue',Arial,sans-serif">
                <tr><td align="center" style="padding:28px 14px">
                <table role="presentation" class="athluna-wrap" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%%;border-collapse:separate;border-radius:16px;overflow:hidden;box-shadow:0 8px 32px rgba(15,23,42,0.10)">
                <tr><td class="%s" style="padding:28px 36px;text-align:center;%s">
                <div style="font-size:24px;font-weight:700;color:%s;letter-spacing:-0.02em;line-height:1.2">%s</div>
                <div style="font-size:13px;color:%s;margin-top:8px;letter-spacing:0.04em">%s</div>
                </td></tr>
                <tr><td class="athluna-pad athluna-fade" style="background:#ffffff;padding:32px 36px;font-size:15px;line-height:1.65;color:#334155">
                %s
                </td></tr>
                <tr><td style="background:%s;padding:18px 32px;text-align:center;border-top:1px solid #e2e8f0">
                <div style="font-size:12px;color:%s;line-height:1.5">%s Team · Reply to this email to connect</div>
                </td></tr>
                </table>
                </td></tr>
                </table>
                """.formatted(
                escapeAttr(normalizeVisualStyle(visualStyle)),
                theme.outerBg(),
                headerClass,
                theme.headerStyle(),
                theme.titleColor(),
                brandName,
                theme.subtitleColor(),
                teamLine,
                inner,
                theme.footerBg(),
                theme.footerTextColor(),
                brandName);
    }

    private static String normalizeVisualStyle(String visualStyle) {
        if (!StringUtils.hasText(visualStyle)) {
            return "gradient";
        }
        return visualStyle.toLowerCase();
    }

    private static String renderProductListText(List<MailTemplateAiRequest.SampleProductItem> products) {
        StringBuilder sb = new StringBuilder("--- Sample Products ---\n");
        for (MailTemplateAiRequest.SampleProductItem p : products) {
            sb.append("- ").append(nullToEmpty(p.getTitle()));
            if (StringUtils.hasText(p.getSku())) sb.append(" | SKU: ").append(p.getSku());
            sb.append('\n');
        }
        return sb.toString();
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static String escapeAttr(String s) {
        if (s == null) return "";
        return s.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String rootCauseMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        String m = cur.getMessage();
        if (m == null || m.isBlank()) {
            m = cur.getClass().getSimpleName();
        }
        if (m.length() > 280) m = m.substring(0, 280) + "…";
        return m;
    }

    public MailPersonalizeResponse personalize(MailPersonalizeRequest req) {
        Map<String, Object> inf = loadInfluencer(req.getInfluencerId());
        String brief = nullToEmpty(req.getBrief());
        // 优先 brief 里检测回复上下文（backend-mail 拼进来的）；其次看 purpose 字段
        boolean isReply = brief.contains("REPLY CONTEXT")
                || brief.contains("红人上一封来信")
                || brief.contains("撮合策略")
                || brief.contains("写跟进")
                || "reply".equalsIgnoreCase(req.getPurpose())
                || "reactivate".equalsIgnoreCase(req.getPurpose());
        String prompt;
        if (isReply) {
            prompt = buildReplyPrompt(inf, brief, req.getLanguage());
        } else {
            // 非回复场景：把 purpose 对应的"角色 / 语气 / 信息结构"指令注入到邀约 prompt 里
            String directive = purposeDirective(req.getPurpose());
            prompt = buildScenarioPrompt(inf, brief, req.getLanguage(),
                    nullToEmpty(req.getSubjectTpl()), nullToEmpty(req.getHtmlBodyTpl()),
                    directive);
        }

        MailPersonalizeResponse resp = parseJson(chatLanguageModel.generate(prompt), MailPersonalizeResponse.class, () -> {
            MailPersonalizeResponse f = new MailPersonalizeResponse();
            f.setSubject(String.valueOf(req.getSubjectTpl()));
            f.setHtmlBody(String.valueOf(req.getHtmlBodyTpl()));
            f.setTextBody(String.valueOf(req.getTextBodyTpl()));
            return f;
        });
        resp.setInfluencerId(req.getInfluencerId());
        resp.setDisplayName(asString(inf.get("nick_name"), inf.get("real_name")));
        resp.setHandle(asString(inf.get("default_handle")));
        return resp;
    }

    /**
     * 多场景邮件：根据 purposeDirective 决定语气和信息结构；模版只作可选参考，不强制扩写。
     * 这是 generateTemplate 之外、单红人发送时的主路径。
     */
    private String buildScenarioPrompt(Map<String, Object> inf, String brief, String language,
                                       String subjectTpl, String htmlBodyTpl,
                                       String purposeDirective) {
        return """
                你正在给一位跨境红人写邮件。请严格按下方场景指令的语气和信息结构来写。

                === 场景指令 ===
                %s

                === 红人信息 ===
                %s

                === 运营/合作要点 ===
                %s

                === 语言 ===
                %s

                === 模版（仅作参考，**不要照抄**；如场景不匹配可完全忽略）===
                主题模版：%s
                HTML模版：%s

                === 输出 ===
                输出纯 JSON：{"subject":"...","htmlBody":"...","textBody":"..."}
                - htmlBody 是邮件 HTML 片段，称呼用红人昵称或真实姓名。
                - 不要追踪像素。
                - 控制在 80~150 词内，自然口语化，读起来像真人 BD 写的。

                %s
                """.formatted(
                        purposeDirective,
                        inf,
                        brief,
                        langHint(language),
                        subjectTpl,
                        htmlBodyTpl,
                        HUMAN_VOICE_RULES);
    }

    /** 邀约场景：基于模版扩写一封商务合作邀约邮件（保留旧入口，向后兼容） */
    private String buildInvitePrompt(Map<String, Object> inf, String brief, String language,
                                     String subjectTpl, String htmlBodyTpl) {
        return """
                为以下红人个性化一封商务合作邀约邮件（基于模版扩写，不要照抄模版）。
                红人信息：%s
                合作/活动说明：%s
                语言：%s
                主题模版：%s
                HTML模版：%s

                输出纯 JSON：
                {"subject":"...","htmlBody":"...","textBody":"..."}
                htmlBody 为 HTML，勿含追踪像素；称呼用红人昵称或真实姓名。
                读起来像真人 BD 认真看过账号后写的，不要模版/AI 腔。

                %s
                """.formatted(inf, brief, langHint(language), subjectTpl, htmlBodyTpl, HUMAN_VOICE_RULES);
    }

    /**
     * 回复场景：完全不基于邀约模版，先理解红人上一封来信，再写有针对性的回复。
     * 目标是撮合合作（不是再发一封新邀约）。
     */
    private String buildReplyPrompt(Map<String, Object> inf, String briefWithReplyContext, String language) {
        return """
                你正在帮品牌方撰写一封 **回复邮件**（不是新邀约）。
                请先完整阅读下方往来，理解：红人问了什么、我方已承诺什么、当前卡在哪个环节，
                再写出有针对性的回复，推动合作向前一步（不是重发邀约）。

                红人信息：%s
                语言：%s

                以下是【该红人完整邮件往来 + 合作意向 + 我方意图】：
                %s

                === 第一步：判断沟通阶段（在心里完成，不要输出） ===
                从往来中判断当前处于：初次接触 / 意向确认 / 条款协商 / 合同书面确认 /
                寄样物流 / 内容创作 / 结算复盘 中的哪一阶段。

                === 第二步：规划唯一合理的下一步 ===
                根据阶段提出**一个**清晰 CTA，必须与往来内容一致，禁止机械套用固定流程。

                商务逻辑（必须遵守）：
                - 合作条款、佣金、合同/书面确认必须在寄样、打款、正式合作执行**之前**完成。
                - **禁止**建议「收到样品后再开会敲定合同」「先寄样再谈条款」等顺序颠倒的流程。
                - 红人尚未确认意向 → 答疑或确认兴趣，不要直接寄样或签合约。
                - 条款有分歧 → 先回应分歧，再提议具体确认方式。
                - 条款已确认 → 才进入寄样地址/物流或内容排期。

                回复要求：
                1. **必须回应 ★ 标记邮件及红人提出的每个具体问题**，不要忽略。
                2. 用与红人来信相同的语言（英文回英文，中文回中文）。
                3. 第一句直接回应红人最关心的点，不要「很高兴收到您的来信」等模板客套。
                4. 用具体细节回应疑问（佣金、样品、产品、条款等），不要空泛承诺。
                5. 结尾给出**一个**与当前阶段匹配的下一步，让红人知道该做什么。
                6. 控制在 80~150 词，像真人同事回邮件：自然、有温度、不端着。
                7. 称呼用红人昵称或真实姓名；落款用品牌名或发信人名字（可用 First name + Brand）。
                8. **不要重复邀约邮件全文**，红人已经看过才会回信。
                9. 可适度 mirror 红人的语气（对方 casual 你就 casual 一点，对方 formal 就 formal 一点）。

                %s

                输出纯 JSON：
                {"subject":"Re: 原信主题（保留 Re: 前缀）","htmlBody":"...","textBody":"..."}
                htmlBody 是简洁 HTML（段落 + 链接即可），不要追踪像素。
                """.formatted(inf, langHint(language), briefWithReplyContext, HUMAN_VOICE_RULES);
    }

    public List<MailPersonalizeResponse> personalizeBatch(List<MailPersonalizeRequest> requests) {
        List<MailPersonalizeResponse> list = new ArrayList<>();
        for (MailPersonalizeRequest r : requests) {
            try {
                list.add(personalize(r));
            } catch (Exception e) {
                log.warn("personalize failed influencerId={}", r.getInfluencerId(), e);
                MailPersonalizeResponse err = new MailPersonalizeResponse();
                err.setInfluencerId(r.getInfluencerId());
                err.setSubject(r.getSubjectTpl());
                err.setHtmlBody(r.getHtmlBodyTpl());
                err.setTextBody(r.getTextBodyTpl());
                list.add(err);
            }
        }
        return list;
    }

    public MailIntentAnalyzeResponse analyzeIntent(MailIntentAnalyzeRequest req) {
        Map<String, Object> inf = req.getInfluencerId() != null
                ? loadInfluencer(req.getInfluencerId())
                : Map.of();
        String prompt = """
                你正在「红人邮件」模块里分析一封【红人回复邮件】的合作意向。
                运营和红人的沟通渠道就是邮件线程，不要建议改用 Instagram/TikTok/微信/电话等其他渠道，
                除非正文明确说「请通过 IG 联系我」。

                红人档案：%s
                邮件主题：%s
                往来摘要：%s
                回复正文：
                %s

                输出纯 JSON：
                {
                  "intentScore": 0-100,
                  "intentLabel": "STRONG|MEDIUM|WEAK|REJECT|UNCLEAR",
                  "summaryZh": "1句口语化 briefing，转述对方回信要点",
                  "keySignals": ["信号1","信号2"],
                  "suggestedActionZh": "1句口语化下一步，直接说回邮件做什么",
                  "replyLanguage": "en|es|...",
                  "quotedPhrase": "原文关键句"
                }

                评分标准：明确愿意合作/询问佣金样品=高分；礼貌观望=中；明确拒绝=REJECT；过短模糊=UNCLEAR。

                suggestedActionZh 必须遵守：
                - 结合往来摘要判断当前沟通阶段，只建议**一个**与阶段匹配的邮件动作
                - 条款/合同确认必须在寄样、打款之前；禁止建议「收样后再签合同」等顺序颠倒的流程
                - 只写在本邮件线程里该怎么回，例如：「先确认佣金比例，再要寄样地址」
                - 可写具体要在邮件里问的问题或要补充的信息
                - 禁止写「通过邮件或 Instagram 私信跟进」——已经在邮件里了
                - 禁止换渠道；最多 1 句话， actionable

                %s
                %s
                """.formatted(
                inf,
                nullToEmpty(req.getSubject()),
                nullToEmpty(req.getThreadSummary()),
                nullToEmpty(req.getReplyBody()),
                INTENT_BRIEF_RULES,
                HUMAN_VOICE_RULES);

        return parseJson(chatLanguageModel.generate(prompt), MailIntentAnalyzeResponse.class, () -> {
            MailIntentAnalyzeResponse f = new MailIntentAnalyzeResponse();
            f.setIntentScore(50);
            f.setIntentLabel("UNCLEAR");
            f.setSummaryZh("这封回信我还看不太准，你打开正文瞅一眼");
            f.setKeySignals(List.of());
            f.setSuggestedActionZh("回邮件问一句对方是否愿意合作，以及最关心佣金还是寄样");
            f.setReplyLanguage("unknown");
            return f;
        });
    }

    private Map<String, Object> loadInfluencer(Long influencerId) {
        if (influencerId == null) {
            return Map.of();
        }
        String url = influencerServiceUrl + "/internal/api/influencers/batch-details";
        List<Map<String, Object>> rows = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(List.of(influencerId)),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {
                }).getBody();
        if (rows == null || rows.isEmpty()) {
            return Map.of("id", influencerId);
        }
        return rows.get(0);
    }

    private <T> T parseJson(String raw, Class<T> type, java.util.function.Supplier<T> fallback) {
        try {
            String json = extractJson(raw);
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.warn("AI JSON parse failed: {}", e.getMessage());
            return fallback.get();
        }
    }

    private String extractJson(String raw) {
        if (raw == null) {
            return "{}";
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceAll("^```[a-zA-Z]*\\n?", "").replaceAll("```\\s*$", "").trim();
        }
        Matcher m = JSON_BLOCK.matcher(trimmed);
        if (m.find()) {
            return m.group();
        }
        return trimmed;
    }

    private static String buildBrief(MailTemplateAiRequest req) {
        if (StringUtils.hasText(req.getBrief())) {
            return req.getBrief();
        }
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(req.getBrand())) {
            sb.append("品牌：").append(req.getBrand()).append('\n');
        }
        if (StringUtils.hasText(req.getCommissionRate())) {
            sb.append("佣金率：").append(req.getCommissionRate()).append('\n');
        }
        if (req.getSampleProducts() != null && !req.getSampleProducts().isEmpty()) {
            sb.append("可提供样品商品：\n");
            for (MailTemplateAiRequest.SampleProductItem p : req.getSampleProducts()) {
                sb.append("- ").append(nullToEmpty(p.getTitle()));
                if (StringUtils.hasText(p.getSku())) {
                    sb.append(" (SKU: ").append(p.getSku()).append(')');
                }
                if (StringUtils.hasText(p.getSpu())) {
                    sb.append(" SPU: ").append(p.getSpu());
                }
                if (StringUtils.hasText(resolveProductImageUrl(p))) {
                    sb.append(" 商品图: ").append(resolveProductImageUrl(p));
                }
                sb.append('\n');
            }
        }
        if (StringUtils.hasText(req.getExtraNotes())) {
            sb.append("补充说明：").append(req.getExtraNotes()).append('\n');
        }
        if (StringUtils.hasText(req.getVisualStyle())) {
            sb.append("页面风格：").append(normalizeVisualStyle(req.getVisualStyle())).append('\n');
        }
        return sb.toString().trim();
    }

    private static String langHint(String lang) {
        if (!StringUtils.hasText(lang) || "auto".equalsIgnoreCase(lang)) {
            return "英文（默认）";
        }
        return switch (lang.toLowerCase()) {
            case "zh", "cn" -> "中文";
            case "fr" -> "法语";
            case "de" -> "德语";
            case "ar" -> "阿拉伯语";
            case "el" -> "希腊语";
            case "es" -> "西班牙语";
            case "en" -> "英语";
            default -> "英语";
        };
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String asString(Object... vals) {
        for (Object v : vals) {
            if (v != null && StringUtils.hasText(v.toString())) {
                return v.toString();
            }
        }
        return "";
    }
}
