#!/usr/bin/env python3
"""
随机生成商品（shopify_products + variants）和折扣码（shopify_discounts）测试数据。

用法:
  py database_scripts/seed_products_discounts.py
  py database_scripts/seed_products_discounts.py --products 60 --discounts 80
  py database_scripts/seed_products_discounts.py --reset       # 先清空已有的测试数据再生成
"""

from __future__ import annotations

import argparse
import json
import os
import random
import string
from datetime import datetime, timedelta

try:
    import pymysql
except ImportError:
    raise SystemExit("请先安装: pip install pymysql")

DB_CONFIG = {
    "host": os.getenv("DB_HOST", "localhost"),
    "port": int(os.getenv("DB_PORT", "3306")),
    "user": os.getenv("DB_USER", "root"),
    "password": os.getenv("DB_PASSWORD", "Yaoxingxuanlv-39"),
    "database": os.getenv("DB_NAME", "hongrenfuwu"),
    "charset": "utf8mb4",
}

# ============================== 商品池子 ==============================

PRODUCT_CATALOG = [
    ("Athluna Pro Yoga Leggings", "Apparel", "Athluna", ["yoga", "leggings", "sportswear"]),
    ("HyperFlex Sports Bra", "Apparel", "Athluna", ["bra", "fitness", "sportswear"]),
    ("CloudRun Running Shoes", "Footwear", "Athluna", ["running", "shoes", "sneakers"]),
    ("Daily Training Tank Top", "Apparel", "Athluna", ["tank", "training", "casual"]),
    ("Aurora Workout Set", "Apparel", "Athluna", ["set", "workout", "matching"]),
    ("Velocity Cycling Shorts", "Apparel", "Athluna", ["cycling", "shorts", "summer"]),
    ("Ember Long Sleeve Tee", "Apparel", "Athluna", ["tee", "long-sleeve", "basics"]),
    ("Frostline Down Jacket", "Outerwear", "Athluna", ["jacket", "winter", "down"]),
    ("Pebble Crossbody Bag", "Accessories", "Athluna", ["bag", "accessories", "everyday"]),
    ("Halcyon Yoga Mat", "Equipment", "Athluna", ["yoga", "mat", "equipment"]),
    ("PulseWave Smart Bottle", "Equipment", "Athluna", ["bottle", "smart", "hydration"]),
    ("Aurora Sports Cap", "Accessories", "Athluna", ["cap", "headwear", "sun"]),
    ("Stride Light Sneakers", "Footwear", "Athluna", ["sneakers", "lightweight", "training"]),
    ("Glide Compression Tights", "Apparel", "Athluna", ["tights", "compression", "recovery"]),
    ("Mira Seamless Bra", "Apparel", "Athluna", ["bra", "seamless", "everyday"]),
    ("Element Lounge Hoodie", "Apparel", "Athluna", ["hoodie", "lounge", "casual"]),
    ("Whisper Eco Tote", "Accessories", "Athluna", ["tote", "eco", "casual"]),
    ("Nova Cross-training Shoes", "Footwear", "Athluna", ["cross-training", "shoes", "gym"]),
    ("Sienna Beach Wrap", "Apparel", "Athluna", ["wrap", "beach", "summer"]),
    ("Voltage Sweat Headband", "Accessories", "Athluna", ["headband", "running", "sweat"]),
    ("Drift Recovery Sandals", "Footwear", "Athluna", ["sandals", "recovery", "summer"]),
    ("Loft Knit Beanie", "Accessories", "Athluna", ["beanie", "winter", "knit"]),
    ("Cascade Rain Shell", "Outerwear", "Athluna", ["shell", "rain", "outerwear"]),
    ("Echo Wireless Earbuds Strap", "Accessories", "Athluna", ["strap", "earbuds", "tech"]),
    ("Trail Storm Backpack", "Accessories", "Athluna", ["backpack", "outdoor", "trail"]),
    ("Coast Linen Shirt", "Apparel", "Athluna", ["shirt", "linen", "summer"]),
    ("Aero Quick-Dry Polo", "Apparel", "Athluna", ["polo", "quickdry", "training"]),
    ("Mosaic Crop Top", "Apparel", "Athluna", ["crop", "summer", "training"]),
    ("Nimbus Cloud Slides", "Footwear", "Athluna", ["slides", "recovery", "casual"]),
    ("Forge Iron Dumbbell Set", "Equipment", "Athluna", ["dumbbell", "equipment", "strength"]),
    ("Apex Resistance Bands", "Equipment", "Athluna", ["bands", "resistance", "home-gym"]),
    ("Pulse Heart Rate Strap", "Equipment", "Athluna", ["strap", "heart-rate", "tech"]),
    ("Lumen Reflective Vest", "Apparel", "Athluna", ["vest", "reflective", "running"]),
    ("Solace Sleep Tee", "Apparel", "Athluna", ["tee", "sleep", "lounge"]),
    ("Onyx Training Gloves", "Accessories", "Athluna", ["gloves", "training", "gym"]),
    ("Wave Yoga Block Pair", "Equipment", "Athluna", ["block", "yoga", "pair"]),
    ("Solstice Sun Visor", "Accessories", "Athluna", ["visor", "sun", "outdoor"]),
    ("Aurora Two-Tone Joggers", "Apparel", "Athluna", ["joggers", "casual", "two-tone"]),
    ("Pinnacle Hiking Socks", "Apparel", "Athluna", ["socks", "hiking", "outdoor"]),
    ("Helio Sun Glasses Strap", "Accessories", "Athluna", ["glasses", "strap", "sun"]),
]

COLOR_OPTIONS = ["Black", "White", "Navy", "Sand", "Olive", "Rose", "Lilac", "Sky"]
SIZE_OPTIONS = ["XS", "S", "M", "L", "XL", "XXL"]
PRODUCT_STATUSES = ["active", "active", "active", "active", "draft"]  # 多数 active
CURRENCIES = ["USD", "USD", "USD", "EUR", "GBP"]

# ============================== 折扣码池子 ==============================

DISCOUNT_PREFIXES = [
    "EMMA", "OLIVIA", "AVA", "SOPHIA", "MIA", "HARPER", "ELLA", "AMELIA",
    "ZOE", "LILY", "CHLOE", "ALYSSA", "MADELYN", "RILEY", "AUDREY", "BROOKLYN",
    "FALL", "SUMMER", "WINTER", "SPRING", "BFCM", "VIP", "INSIDER", "EARLY",
    "WELCOME", "NEW", "FRESH", "GLOW", "BOOST", "ENERGY", "STRIDE", "PULSE",
]
DISCOUNT_TYPES = [
    # (discount_type, value_type)
    ("PERCENTAGE", "percentage"),
    ("PERCENTAGE", "percentage"),
    ("PERCENTAGE", "percentage"),
    ("FIXED_AMOUNT", "fixed_amount"),
]
DISCOUNT_STATUSES = ["ACTIVE", "ACTIVE", "ACTIVE", "ACTIVE", "EXPIRED", "SCHEDULED"]


# ============================== 工具函数 ==============================

def rand_handle(title: str) -> str:
    base = "".join(c.lower() if c.isalnum() else "-" for c in title).strip("-")
    while "--" in base:
        base = base.replace("--", "-")
    return f"{base}-{random.randint(1000, 9999)}"


def rand_sku(prefix: str = "ATL") -> str:
    return f"{prefix}-{random.randint(10000, 99999)}-{random.choice(string.ascii_uppercase)}"


def rand_image_url(seed: str) -> str:
    code = abs(hash(seed)) % 1000
    return f"https://picsum.photos/seed/athluna-{code}/600/600"


def rand_datetime(days_back: int = 180) -> datetime:
    start = datetime.now() - timedelta(days=days_back)
    delta = datetime.now() - start
    sec = random.randint(0, int(delta.total_seconds()))
    return start + timedelta(seconds=sec)


def ensure_store(cur) -> int:
    """确保至少有一个 shopify_stores，返回 store_id。"""
    cur.execute("SELECT id FROM shopify_stores ORDER BY id LIMIT 1")
    row = cur.fetchone()
    if row:
        return int(row[0])

    cur.execute(
        """
        INSERT INTO shopify_stores
        (store_name, store_url_encrypted, access_token_encrypted, notes,
         status, connection_verified, shop_domain, plan_name,
         currency, timezone, country_code, created_at, updated_at, myshopify_domain)
        VALUES
        (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW(), %s)
        """,
        (
            "Athluna Official Store",
            "ENCRYPTED::athluna.myshopify.com",      # 加密字段占位，前端不解密展示
            "ENCRYPTED::shpat_DEMO_TOKEN_PLACEHOLDER",
            "Demo seed store - 测试数据用",
            "ACTIVE",
            1,
            "athluna.myshopify.com",
            "Shopify Plus",
            "USD",
            "America/Los_Angeles",
            "US",
            "athluna.myshopify.com",
        ),
    )
    return int(cur.lastrowid)


def gen_variants(product_id: int, shopify_product_id: int, base_price: float, base_sku: str) -> list[dict]:
    variants: list[dict] = []
    # 1-3 颜色，每个颜色 2-4 尺码
    colors = random.sample(COLOR_OPTIONS, k=random.randint(1, 3))
    sizes = random.sample(SIZE_OPTIONS, k=random.randint(2, 4))
    pos = 1
    for color in colors:
        for size in sizes:
            price_delta = random.uniform(-3, 5)
            v_price = round(max(5, base_price + price_delta), 2)
            variants.append({
                "shopify_variant_id": random.randint(10**13, 10**14 - 1),
                "title": f"{color} / {size}",
                "sku": f"{base_sku}-{color[:2].upper()}-{size}",
                "barcode": str(random.randint(10**11, 10**12 - 1)),
                "price": v_price,
                "compare_at_price": round(v_price * random.uniform(1.05, 1.4), 2),
                "inventory_quantity": random.randint(0, 250),
                "weight": round(random.uniform(0.1, 1.5), 2),
                "weight_unit": "kg",
                "option1": color,
                "option2": size,
                "position": pos,
            })
            pos += 1
    return variants


# ============================== 主流程 ==============================

def seed_products(cur, store_id: int, count: int) -> int:
    inserted = 0
    used_shopify_ids: set[int] = set()
    cur.execute("SELECT shopify_product_id FROM shopify_products")
    for (sid,) in cur.fetchall():
        used_shopify_ids.add(int(sid))

    for _ in range(count):
        title, ptype, vendor, tag_list = random.choice(PRODUCT_CATALOG)
        title_full = f"{title} {random.choice(['','Pro','Lite','V2',''])}"
        title_full = title_full.strip()
        handle = rand_handle(title_full)
        base_price = round(random.uniform(15, 220), 2)
        compare_at = round(base_price * random.uniform(1.05, 1.4), 2)
        status = random.choice(PRODUCT_STATUSES)
        currency = random.choice(CURRENCIES)
        spu = f"SPU-{random.randint(10000, 99999)}"
        sku = rand_sku()
        img = rand_image_url(handle)
        created = rand_datetime(360)
        updated = created + timedelta(days=random.randint(0, 60))
        published = created + timedelta(hours=random.randint(0, 48)) if status == "active" else None

        while True:
            shop_pid = random.randint(10**13, 10**14 - 1)
            if shop_pid not in used_shopify_ids:
                used_shopify_ids.add(shop_pid)
                break

        cur.execute(
            """
            INSERT INTO shopify_products
            (shopify_product_id, store_id, title, body_html, product_type, vendor,
             handle, status, price, compare_at_price, currency, total_inventory,
             spu, sku, main_image, image_url, online_store_url,
             tags, options_json, variants_json,
             created_at, updated_at, published_at, sync_at, synced_at)
            VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,NOW(),NOW())
            """,
            (
                shop_pid, store_id, title_full,
                f"<p>{title_full} - high-performance everyday gear.</p>",
                ptype, vendor, handle, status,
                base_price, compare_at, currency, random.randint(0, 1500),
                spu, sku, img, img,
                f"https://athluna.myshopify.com/products/{handle}",
                ",".join(tag_list),
                json.dumps([{"name": "Color", "values": COLOR_OPTIONS[:3]},
                            {"name": "Size", "values": SIZE_OPTIONS[:4]}], ensure_ascii=False),
                None,  # variants_json 先 None，下面单独插 variants 表
                created, updated, published,
            ),
        )
        product_id = cur.lastrowid

        variants = gen_variants(product_id, shop_pid, base_price, sku)
        for v in variants:
            cur.execute(
                """
                INSERT INTO shopify_product_variants
                (product_id, shopify_variant_id, shopify_product_id, title, sku, barcode,
                 price, compare_at_price, inventory_quantity, weight, weight_unit,
                 option1, option2, option3, image_url, position, created_at, updated_at)
                VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
                """,
                (
                    product_id, v["shopify_variant_id"], shop_pid,
                    v["title"], v["sku"], v["barcode"],
                    v["price"], v["compare_at_price"], v["inventory_quantity"],
                    v["weight"], v["weight_unit"],
                    v["option1"], v.get("option2"), None,
                    img, v["position"],
                    created, updated,
                ),
            )

        # 把 variants_json 回写到 product
        variants_json_payload = [{
            "id": v["shopify_variant_id"],
            "title": v["title"],
            "sku": v["sku"],
            "price": v["price"],
            "inventoryQuantity": v["inventory_quantity"],
            "options": [v["option1"], v.get("option2")],
        } for v in variants]
        cur.execute(
            "UPDATE shopify_products SET variants_json=%s WHERE id=%s",
            (json.dumps(variants_json_payload, ensure_ascii=False), product_id),
        )
        inserted += 1

    return inserted


def seed_discounts(cur, store_id: int, count: int) -> int:
    inserted = 0
    used_codes: set[str] = set()
    cur.execute("SELECT code FROM shopify_discounts")
    for (c,) in cur.fetchall():
        used_codes.add(c.upper())

    # 拿一些 influencer id 让一部分折扣码绑定红人
    cur.execute("SELECT id FROM influencer ORDER BY RAND() LIMIT 200")
    influencer_ids = [int(r[0]) for r in cur.fetchall()]

    used_shopify_pr_ids: set[int] = set()
    used_shopify_dc_ids: set[int] = set()
    cur.execute("SELECT shopify_price_rule_id, shopify_discount_id FROM shopify_discounts")
    for pr, dc in cur.fetchall():
        if pr is not None:
            used_shopify_pr_ids.add(int(pr))
        if dc is not None:
            used_shopify_dc_ids.add(int(dc))

    for _ in range(count):
        # 生成不重复的 code
        while True:
            prefix = random.choice(DISCOUNT_PREFIXES)
            code = f"{prefix}{random.randint(5, 35)}"
            if code.upper() not in used_codes:
                used_codes.add(code.upper())
                break

        dtype, vtype = random.choice(DISCOUNT_TYPES)
        if vtype == "percentage":
            value = -round(random.choice([5, 8, 10, 12, 15, 18, 20, 25, 30]), 2)
        else:
            value = -round(random.choice([5, 10, 15, 20, 25, 30, 50]), 2)

        status = random.choice(DISCOUNT_STATUSES)
        starts_at = rand_datetime(180)
        if status == "EXPIRED":
            ends_at = starts_at + timedelta(days=random.randint(5, 30))
        elif status == "SCHEDULED":
            starts_at = datetime.now() + timedelta(days=random.randint(3, 30))
            ends_at = starts_at + timedelta(days=random.randint(15, 60))
        else:
            ends_at = datetime.now() + timedelta(days=random.randint(15, 120))

        usage_count = random.randint(0, 200) if status == "ACTIVE" else random.randint(0, 50)

        # 部分（约 70%）绑定红人，并写入 influencer_discount_binding
        influencer_id = None
        if influencer_ids and random.random() < 0.7:
            influencer_id = random.choice(influencer_ids)

        while True:
            shop_pr_id = random.randint(10**13, 10**14 - 1)
            if shop_pr_id not in used_shopify_pr_ids:
                used_shopify_pr_ids.add(shop_pr_id)
                break
        while True:
            shop_dc_id = random.randint(10**13, 10**14 - 1)
            if shop_dc_id not in used_shopify_dc_ids:
                used_shopify_dc_ids.add(shop_dc_id)
                break

        cur.execute(
            """
            INSERT INTO shopify_discounts
            (store_id, store_name, shopify_price_rule_id, shopify_discount_id, shopify_gid,
             shopify_typename, code, title, description, discount_type, value, value_type,
             target_type, target_selection, allocation_method, customer_selection,
             usage_limit, once_per_customer, usage_count,
             starts_at, ends_at, status, created_at, updated_at, sync_at,
             influencer_id, conversion_order_count, source_type)
            VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,NOW(),NOW(),NOW(),%s,%s,%s)
            """,
            (
                store_id, "Athluna Official Store", shop_pr_id, shop_dc_id,
                f"gid://shopify/DiscountCodeNode/{shop_dc_id}",
                "DiscountCodeBasic",
                code, f"{code} 折扣码", f"测试折扣码 - {code}",
                dtype, value, vtype,
                "LINE_ITEM", "ALL", "ACROSS", "ALL",
                random.choice([None, 100, 500, 1000, 5000]),
                random.choice([0, 0, 0, 1]),
                usage_count,
                starts_at, ends_at, status,
                influencer_id, random.randint(0, max(1, usage_count // 2)) if status == "ACTIVE" else 0,
                "SHOPIFY",
            ),
        )

        if influencer_id is not None:
            # 同步到 influencer_discount_binding（如果已存在则忽略）
            cur.execute(
                """
                INSERT IGNORE INTO influencer_discount_binding
                (commission_rate, created_at, discount_code, influencer_id, status, updated_at)
                VALUES (%s, NOW(), %s, %s, %s, NOW())
                """,
                (round(random.uniform(5, 25), 2), code, influencer_id, "ACTIVE"),
            )

        inserted += 1
    return inserted


def reset_data(cur):
    """清空 seed 生成过的折扣码 / 商品，避免重复跑时膨胀。"""
    cur.execute("SET FOREIGN_KEY_CHECKS=0")
    cur.execute("DELETE FROM shopify_product_variants")
    cur.execute("DELETE FROM shopify_products")
    cur.execute("DELETE FROM shopify_discounts")
    cur.execute("SET FOREIGN_KEY_CHECKS=1")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--products", type=int, default=40, help="生成商品数量")
    parser.add_argument("--discounts", type=int, default=60, help="生成折扣码数量")
    parser.add_argument("--reset", action="store_true", help="先清空 shopify_products/variants/discounts 再生成")
    args = parser.parse_args()

    random.seed()

    conn = pymysql.connect(**DB_CONFIG, autocommit=False)
    try:
        with conn.cursor() as cur:
            if args.reset:
                print("[reset] 清空 shopify_products / shopify_product_variants / shopify_discounts ...")
                reset_data(cur)

            store_id = ensure_store(cur)
            print(f"[store] 使用 store_id={store_id}")

            p_count = seed_products(cur, store_id, args.products)
            print(f"[products] 已生成 {p_count} 个商品（含 variants）")

            d_count = seed_discounts(cur, store_id, args.discounts)
            print(f"[discounts] 已生成 {d_count} 个折扣码")

            conn.commit()
            print("[done] 提交成功")
    except Exception as e:
        conn.rollback()
        print(f"[error] {e}")
        raise
    finally:
        conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
