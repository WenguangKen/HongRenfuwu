#!/usr/bin/env python3
"""
随机生成转化单、样品单测试数据（各 200-500 条）。
同时写入 orders 主表，保证列表与详情可正常关联。

用法:
  python database_scripts/seed_random_orders.py
  python database_scripts/seed_random_orders.py --conversion 300 --sample 400
"""

from __future__ import annotations

import argparse
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

FIRST_NAMES = [
    "Emma", "Olivia", "Ava", "Sophia", "Mia", "Charlotte", "Amelia", "Harper",
    "James", "Michael", "Robert", "David", "William", "Joseph", "Thomas", "Daniel",
    "Ashley", "Jessica", "Sarah", "Jennifer", "Amanda", "Stephanie", "Nicole", "Elizabeth",
]
LAST_NAMES = [
    "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
    "Rodriguez", "Martinez", "Hernandez", "Lopez", "Wilson", "Anderson", "Taylor", "Thomas",
]
CITIES = [
    ("Los Angeles", "California", "US"),
    ("New York", "New York", "US"),
    ("Chicago", "Illinois", "US"),
    ("Houston", "Texas", "US"),
    ("Phoenix", "Arizona", "US"),
    ("Seattle", "Washington", "US"),
    ("Miami", "Florida", "US"),
    ("Denver", "Colorado", "US"),
]
FULFILLMENT_STATUSES = [
    ("pending", None, None),
    ("pending_ship", None, None),
    ("in_transit", "in_transit", None),
    ("delivered", "delivered", True),
    ("cancelled", "cancelled", None),
]
SAMPLE_REASONS = ["合作寄样", "新品测评", "活动赞助", "内容拍摄", "尺码确认", "二次补样"]
TRACKING_COMPANIES = ["4PX", "SF Express", "DHL", "FedEx", "UPS", "USPS"]


def rand_phone() -> str:
    return f"+1{random.randint(2000000000, 9999999999)}"


def rand_email(first: str, last: str) -> str:
    suffix = "".join(random.choices(string.ascii_lowercase + string.digits, k=4))
    return f"{first.lower()}.{last.lower()}{suffix}@example.com"


def rand_datetime(days_back: int = 180) -> datetime:
    start = datetime.now() - timedelta(days=days_back)
    delta = datetime.now() - start
    sec = random.randint(0, int(delta.total_seconds()))
    return start + timedelta(seconds=sec)


def pick_status() -> tuple[str, str | None, str | None, datetime | None]:
    key, display, delivered_flag = random.choice(FULFILLMENT_STATUSES)
    financial = random.choice(["paid", "paid", "paid", "pending", "partially_refunded"])
    delivered_at = rand_datetime(90) if delivered_flag else None
    in_transit_at = rand_datetime(60) if key == "in_transit" else None
    return key, display or key, financial, delivered_at, in_transit_at


def load_reference_data(conn) -> dict:
    with conn.cursor() as cur:
        cur.execute("SELECT COALESCE(MAX(id), 0) FROM orders")
        max_order_id = cur.fetchone()[0]

        cur.execute("SELECT COALESCE(MAX(shopify_order_number), 900000) FROM orders")
        max_order_number = cur.fetchone()[0]

        cur.execute(
            "SELECT store_id, store_name, shop_domain FROM orders WHERE store_id IS NOT NULL LIMIT 1"
        )
        store = cur.fetchone()
        if not store:
            cur.execute("SELECT id, store_name, shop_domain FROM shopify_stores LIMIT 1")
            s2 = cur.fetchone()
            store = (s2[0], s2[1], s2[2]) if s2 else (2, "正式店铺", "shopathluna.com")

        cur.execute(
            """
            SELECT i.id, COALESCE(i.nick_name, i.real_name, CONCAT('Influencer-', i.id)) AS name,
                   b.discount_code, COALESCE(i.commission_rate, 18)
            FROM influencer i
            LEFT JOIN influencer_discount_binding b ON b.influencer_id = i.id
            ORDER BY i.id
            LIMIT 500
            """
        )
        influencers = cur.fetchall()
        if not influencers:
            raise RuntimeError("数据库中没有红人数据，请先导入 influencer 记录")

    return {
        "max_order_id": max_order_id,
        "max_order_number": max_order_number,
        "store_id": store[0],
        "store_name": store[1],
        "shop_domain": store[2] or "shopathluna.com",
        "influencers": influencers,
    }


def insert_orders_batch(conn, rows: list[tuple]) -> list[int]:
    sql = """
        INSERT INTO orders (
            store_id, store_name, shop_domain, shopify_order_id, shopify_order_number, name,
            created_at_shopify, processed_at_shopify, financial_status, fulfillment_status,
            currency, total_shipping_price, total_price, customer_email, customer_first_name,
            customer_last_name, shipping_name, shipping_phone, shipping_address1, shipping_city,
            shipping_province, shipping_country, shipping_country_code, shipping_zip,
            discount_codes, tags, order_source, sync_status, influencer_id, influencer_name,
            influencer_discount_code, fulfillment_display_status, delivered_at, in_transit_at,
            tracking_number, email, is_draft
        ) VALUES (
            %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s
        )
    """
    ids: list[int] = []
    with conn.cursor() as cur:
        for row in rows:
            cur.execute(sql, row)
            ids.append(cur.lastrowid)
    return ids


def build_order_row(
    ref: dict,
    seq: int,
    kind: str,
    influencer: tuple,
    is_sample: bool,
) -> tuple:
    inf_id, inf_name, discount_code, commission_rate = influencer
    first = random.choice(FIRST_NAMES)
    last = random.choice(LAST_NAMES)
    city, province, country_code = random.choice(CITIES)
    order_no = ref["max_order_number"] + seq
    shopify_gid = f"gid://shopify/Order/seed_{kind}_{order_no}"
    name = f"#{order_no}"
    created = rand_datetime(120)
    processed = created + timedelta(hours=random.randint(1, 48))
    fulfill_key, fulfill_display, financial, delivered_at, in_transit_at = pick_status()

    shipping = round(random.uniform(0, 9.9), 2)
    total = round(random.uniform(29, 299), 2)
    address = f"{random.randint(100, 9999)} {random.choice(['Oak', 'Maple', 'Pine', 'Cedar'])} St"
    zip_code = str(random.randint(10000, 99999))
    email = rand_email(first, last)
    tracking = f"{random.choice(TRACKING_COMPANIES)}{random.randint(10**12, 10**13 - 1)}"
    tags = '["influencer order"]' if is_sample else '["seed conversion"]'
    discount = None if is_sample else (discount_code or f"CODE{inf_id}")

    return (
        ref["store_id"],
        ref["store_name"],
        ref["shop_domain"],
        shopify_gid,
        order_no,
        name,
        created,
        processed,
        financial,
        "fulfilled" if fulfill_key == "delivered" else fulfill_key,
        "USD",
        shipping,
        total,
        email,
        first,
        last,
        f"{first} {last}",
        rand_phone(),
        address,
        city,
        province,
        "United States",
        country_code,
        zip_code,
        discount,
        tags,
        "seed_data",
        "synced",
        inf_id,
        inf_name,
        discount,
        fulfill_display,
        delivered_at,
        in_transit_at,
        tracking,
        email,
        0,
    )


def seed_conversion_orders(conn, ref: dict, count: int) -> int:
    influencers = ref["influencers"]
    order_rows = []
    meta = []
    base_seq = 1

    for i in range(count):
        inf = random.choice(influencers)
        order_rows.append(build_order_row(ref, base_seq + i, "conv", inf, False))
        meta.append(inf)

    order_ids = insert_orders_batch(conn, order_rows)

    sql = """
        INSERT INTO influencer_conversion_order (
            order_id, shopify_order_id, shopify_order_number, order_name,
            influencer_id, influencer_name, discount_code, total_price, total_shipping,
            total_refund, commissionable_amount, currency, commission_rate, commission_amount,
            commission_status, financial_status, order_created_at, processed_at,
            fulfillment_display_status, fulfillment_status, delivered_at, in_transit_at,
            tracking_company, tracking_number, customer_email, recipient_name, recipient_phone,
            recipient_address, recipient_country, commission_source, created_at, updated_at
        ) VALUES (
            %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s
        )
    """
    rows = []
    for idx, order_id in enumerate(order_ids):
        row = order_rows[idx]
        inf = meta[idx]
        inf_id, inf_name, discount_code, commission_rate = inf
        total = float(row[12])
        shipping = float(row[11])
        refund = round(random.uniform(0, min(total * 0.3, 50)), 2) if random.random() < 0.08 else 0
        commissionable = max(round(total - shipping - refund, 2), 0)
        rate = float(commission_rate or 18)
        commission_amount = round(commissionable * rate / 100, 2)
        commission_status = random.choice(["pending", "pending", "settled"])
        rows.append(
            (
                order_id,
                row[3],
                row[4],
                row[5],
                inf_id,
                inf_name,
                discount_code or f"SEED{inf_id}",
                total,
                shipping,
                refund,
                commissionable,
                "USD",
                rate,
                commission_amount,
                commission_status,
                row[8],
                row[6],
                row[7],
                row[31],
                row[9],
                row[32],
                row[33],
                random.choice(TRACKING_COMPANIES),
                row[34],
                row[13],
                row[16],
                row[17],
                f"{row[18]}, {row[19]}, {row[20]}, United States",
                "United States",
                "influencer",
                row[6],
                datetime.now(),
            )
        )

    with conn.cursor() as cur:
        cur.executemany(sql, rows)
    return len(rows)


def seed_sample_orders(conn, ref: dict, count: int) -> int:
    influencers = ref["influencers"]
    order_rows = []
    meta = []
    base_seq = 100000

    for i in range(count):
        inf = random.choice(influencers)
        order_rows.append(build_order_row(ref, base_seq + i, "sample", inf, True))
        meta.append(inf)

    order_ids = insert_orders_batch(conn, order_rows)

    sql = """
        INSERT INTO influencer_sample_order (
            order_id, shopify_order_id, shopify_order_number, order_name,
            influencer_id, influencer_name, total_price, currency, sample_reason,
            order_created_at, financial_status, fulfillment_display_status, fulfillment_status,
            delivered_at, in_transit_at, tracking_company, tracking_number, customer_email,
            recipient_name, recipient_phone, recipient_address, recipient_country,
            auto_matched, manual_override, is_draft, cooperation_price, created_at, updated_at
        ) VALUES (
            %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s
        )
    """
    rows = []
    for idx, order_id in enumerate(order_ids):
        row = order_rows[idx]
        inf = meta[idx]
        inf_id, inf_name, _, _ = inf
        total = float(row[12])
        rows.append(
            (
                order_id,
                row[3],
                row[4],
                row[5],
                inf_id,
                inf_name,
                total,
                "USD",
                random.choice(SAMPLE_REASONS),
                row[6],
                row[8],
                row[31],
                row[9],
                row[32],
                row[33],
                random.choice(TRACKING_COMPANIES),
                row[34],
                row[13],
                row[16],
                row[17],
                f"{row[18]}, {row[19]}, {row[20]}, United States",
                "United States",
                random.choice([0, 1]),
                0,
                0,
                round(random.uniform(50, 500), 2) if random.random() < 0.5 else None,
                row[6],
                datetime.now(),
            )
        )

    with conn.cursor() as cur:
        cur.executemany(sql, rows)
    return len(rows)


def main() -> None:
    parser = argparse.ArgumentParser(description="随机生成转化单/样品单")
    parser.add_argument("--conversion", type=int, default=0, help="转化单数量，0 表示随机 200-500")
    parser.add_argument("--sample", type=int, default=0, help="样品单数量，0 表示随机 200-500")
    args = parser.parse_args()

    conv_count = args.conversion or random.randint(200, 500)
    sample_count = args.sample or random.randint(200, 500)

    print(f"准备生成: 转化单 {conv_count} 条, 样品单 {sample_count} 条")
    print(f"连接数据库 {DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}")

    conn = pymysql.connect(**DB_CONFIG)
    try:
        ref = load_reference_data(conn)
        print(f"参考店铺: {ref['store_name']} (store_id={ref['store_id']})")
        print(f"可用红人数: {len(ref['influencers'])}")

        conn.begin()
        n_conv = seed_conversion_orders(conn, ref, conv_count)
        n_sample = seed_sample_orders(conn, ref, sample_count)
        conn.commit()

        print(f"完成! 已插入转化单 {n_conv} 条, 样品单 {n_sample} 条")
    except Exception as e:
        conn.rollback()
        raise
    finally:
        conn.close()


if __name__ == "__main__":
    main()
