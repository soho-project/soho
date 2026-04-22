#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import argparse
import hashlib
import json
import os
import random
import re
import shutil
import subprocess
import sys
import time
import uuid
from decimal import Decimal, InvalidOperation
from pathlib import Path
from urllib import parse as urllib_parse
from urllib import request as urllib_request
from urllib.error import URLError, HTTPError

try:
    import requests
except Exception:
    requests = None

try:
    from PIL import Image
except Exception:
    Image = None

APP_NAME = "收款小账本"
BASE_W = 413
BASE_H = 795
PROJECT_DIR = Path(__file__).resolve().parent
LOG_DIR = PROJECT_DIR / "logs"
RUNTIME_DIR = PROJECT_DIR / ".runtime"
OUT_IMG = os.getenv("OUT_IMG", str(LOG_DIR / "skb_detail.png"))
OUT_LIST_IMG = os.getenv("OUT_LIST_IMG", str(LOG_DIR / "skb_detail_list.png"))
LOG_FILE = os.getenv("LOG_FILE", str(PROJECT_DIR / "logs" / "skb_detail_ocr.log"))

# 坐标（基于基准窗口尺寸，会按实际窗口缩放）
# 小程序右上角菜单按钮坐标
MINIPROGRAM_MENU_X = int(os.getenv("MINIPROGRAM_MENU_X", "310"))
MINIPROGRAM_MENU_Y = int(os.getenv("MINIPROGRAM_MENU_Y", "40"))
# 小程序菜单中“重新加载小程序”按钮坐标
MINIPROGRAM_RELOAD_X = int(os.getenv("MINIPROGRAM_RELOAD_X", "180"))
MINIPROGRAM_RELOAD_Y = int(os.getenv("MINIPROGRAM_RELOAD_Y", "270"))

# 首页“收款记录”入口坐标（默认水平居中）
RECEIPT_RECORD_X = int(os.getenv("RECEIPT_RECORD_X", str(BASE_W // 2)))
RECEIPT_RECORD_Y = int(os.getenv("RECEIPT_RECORD_Y", "265"))
# 收款记录页“自定义查询”入口坐标
CUSTOM_QUERY_X = int(os.getenv("CUSTOM_QUERY_X", "110"))
CUSTOM_QUERY_Y = int(os.getenv("CUSTOM_QUERY_Y", "115"))
# 自定义查询页“近24小时”快捷筛选坐标
LAST_24H_X = int(os.getenv("LAST_24H_X", "60"))
LAST_24H_Y = int(os.getenv("LAST_24H_Y", "100"))
# 自定义查询页“查询”按钮坐标（默认水平居中）
QUERY_BUTTON_X = int(os.getenv("QUERY_BUTTON_X", str(BASE_W // 2)))
QUERY_BUTTON_Y = int(os.getenv("QUERY_BUTTON_Y", "320"))
OCR_LANG = os.getenv("OCR_LANG", "chi_sim+eng")
LIST_TOP_EXCLUDE_Y = int(os.getenv("LIST_TOP_EXCLUDE_Y", "460"))
DETAIL_OCR_TOP_Y = int(os.getenv("DETAIL_OCR_TOP_Y", "160"))
DETAIL_OCR_BOTTOM_Y = int(os.getenv("DETAIL_OCR_BOTTOM_Y", "430"))
DETAIL_OCR_UPSCALE_PERCENT = int(os.getenv("DETAIL_OCR_UPSCALE_PERCENT", "220"))
REPORT_ENABLED = os.getenv("REPORT_ENABLED", "1").strip() not in ("0", "false", "False")
REPORT_BASE_URL = os.getenv("REPORT_BASE_URL", "").strip()
REPORT_API_PATH = os.getenv("REPORT_API_PATH", "/pay/guest/api/pay/customQr/report").strip()
REPORT_PAY_INFO_ID = os.getenv("REPORT_PAY_INFO_ID", "").strip()
REPORT_PRIVATE_KEY = os.getenv("REPORT_PRIVATE_KEY", "").strip()
REPORT_TIMEOUT_SEC = float(os.getenv("REPORT_TIMEOUT_SEC", "10"))
POLL_ENABLED = os.getenv("POLL_ENABLED", "1").strip() not in ("0", "false", "False")
POLL_BASE_URL = os.getenv("POLL_BASE_URL", "").strip()
POLL_API_PATH = os.getenv("POLL_API_PATH", "/pay/guest/api/pay/customQr/pollOrders").strip()
POLL_PAY_INFO_ID = os.getenv("POLL_PAY_INFO_ID", "").strip()
POLL_PRIVATE_KEY = os.getenv("POLL_PRIVATE_KEY", "").strip()
POLL_LIMIT = int(os.getenv("POLL_LIMIT", "20"))
POLL_WAIT_SECONDS = int(os.getenv("POLL_WAIT_SECONDS", "25"))
POLL_TIMEOUT_SEC = float(os.getenv("POLL_TIMEOUT_SEC", str(POLL_WAIT_SECONDS + 10)))
POLL_INTERVAL_SEC = float(os.getenv("POLL_INTERVAL_SEC", "1"))
POLL_TRIGGER_SLEEP_SEC = float(os.getenv("POLL_TRIGGER_SLEEP_SEC", "15"))
POLL_STATE_FILE = os.getenv("POLL_STATE_FILE", str(RUNTIME_DIR / "custom_qr_poll_state.json"))
# 裁剪模式:
# - absolute: 顶部排除高度按截图绝对像素处理（默认，推荐）
# - scaled: 顶部排除高度按窗口高度相对 BASE_H 缩放
LIST_TOP_EXCLUDE_MODE = os.getenv("LIST_TOP_EXCLUDE_MODE", "absolute").strip().lower()
COMMON_SURNAMES = set(
    "赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜"
    "戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳鲍史唐费"
    "廉岑薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅皮卞齐康伍余元卜顾孟平黄和"
    "穆萧尹姚邵湛汪祁毛禹狄米贝明臧计伏成戴谈宋茅庞熊纪舒屈项祝董梁杜"
    "阮蓝闵席季麻强贾路娄危江童颜郭梅盛林刁钟徐邱骆高夏蔡田樊胡凌霍虞"
    "万支柯昝管卢莫经房裘缪干解应宗丁宣邓郁单杭洪包诸左石崔吉钮龚程嵇"
    "邢滑裴陆荣翁荀羊於惠甄曲家封芮羿储靳汲邴糜松井段富巫乌焦巴弓牧隗"
    "山谷车侯宓蓬全郗班仰秋仲伊宫宁仇栾暴甘斜厉戎祖武符刘景詹束龙叶幸"
    "司韶郜黎蓟薄印宿白怀蒲邰鄂索咸籍赖卓蔺屠蒙池乔阴胥能苍双闻莘党翟"
    "谭贡劳逄姬申扶堵冉宰郦雍郤璩桑桂濮牛寿通边扈燕冀郏浦尚农温别庄晏"
    "柴瞿阎充慕连茹习宦艾鱼容向古易慎戈廖庾终暨居衡步都耿满弘匡国文寇"
    "广禄阙东欧殳沃利蔚越夔隆师巩厍聂晁勾敖融冷訾辛阚那简饶空曾毋沙乜"
    "养鞠须丰巢关蒯相查后荆红游竺权逑盖益桓公"
)
COMPOUND_SURNAMES = ("欧阳", "太史", "端木", "上官", "司马", "东方", "独孤", "南宫", "万俟", "闻人")


def parse_args():
    """
    解析命令行参数。

    :return: 参数对象
    """
    parser = argparse.ArgumentParser(description="自定义二维码支付轮询、OCR 与上报一体化脚本")
    parser.add_argument("--report-base-url", dest="report_base_url", help="上报接口基础地址")
    parser.add_argument("--report-api-path", dest="report_api_path", help="上报接口路径")
    parser.add_argument("--report-pay-info-id", dest="report_pay_info_id", help="上报支付方式 ID")
    parser.add_argument("--report-private-key", dest="report_private_key", help="上报签名私钥")
    parser.add_argument("--report-timeout-sec", dest="report_timeout_sec", type=float, help="上报接口超时秒数")
    parser.add_argument("--poll-base-url", dest="poll_base_url", help="轮询接口基础地址")
    parser.add_argument("--poll-api-path", dest="poll_api_path", help="轮询接口路径")
    parser.add_argument("--poll-pay-info-id", dest="poll_pay_info_id", help="轮询支付方式 ID")
    parser.add_argument("--poll-private-key", dest="poll_private_key", help="轮询签名私钥")
    parser.add_argument("--poll-limit", dest="poll_limit", type=int, help="轮询返回条数")
    parser.add_argument("--poll-wait-seconds", dest="poll_wait_seconds", type=int, help="长轮询等待秒数")
    parser.add_argument("--poll-timeout-sec", dest="poll_timeout_sec", type=float, help="轮询 HTTP 超时秒数")
    parser.add_argument("--poll-interval-sec", dest="poll_interval_sec", type=float, help="两次轮询间隔秒数")
    parser.add_argument("--poll-trigger-sleep-sec", dest="poll_trigger_sleep_sec", type=float, help="命中新支付单后执行检查前等待秒数")
    parser.add_argument("--poll-state-file", dest="poll_state_file", help="轮询状态文件路径")
    parser.add_argument("--log-file", dest="log_file", help="日志文件路径")
    parser.add_argument("--poll", dest="poll_enabled", action="store_true", help="启用轮询模式")
    parser.add_argument("--no-poll", dest="poll_enabled", action="store_false", help="关闭轮询，仅执行一次本地 OCR+上报")
    parser.set_defaults(poll_enabled=None)
    return parser.parse_args()


def init_config(args):
    """
    根据命令行参数和环境变量初始化全局配置。

    :param args: 命令行参数
    :return: 无
    """
    global LOG_FILE
    global REPORT_BASE_URL
    global REPORT_API_PATH
    global REPORT_PAY_INFO_ID
    global REPORT_PRIVATE_KEY
    global REPORT_TIMEOUT_SEC
    global POLL_ENABLED
    global POLL_BASE_URL
    global POLL_API_PATH
    global POLL_PAY_INFO_ID
    global POLL_PRIVATE_KEY
    global POLL_LIMIT
    global POLL_WAIT_SECONDS
    global POLL_TIMEOUT_SEC
    global POLL_INTERVAL_SEC
    global POLL_TRIGGER_SLEEP_SEC
    global POLL_STATE_FILE

    if args.log_file:
        LOG_FILE = args.log_file

    if args.report_base_url:
        REPORT_BASE_URL = args.report_base_url.strip()
    if args.report_api_path:
        REPORT_API_PATH = args.report_api_path.strip()
    if args.report_pay_info_id:
        REPORT_PAY_INFO_ID = args.report_pay_info_id.strip()
    if args.report_private_key:
        REPORT_PRIVATE_KEY = args.report_private_key.strip()
    if args.report_timeout_sec is not None:
        REPORT_TIMEOUT_SEC = args.report_timeout_sec

    if args.poll_enabled is not None:
        POLL_ENABLED = args.poll_enabled
    if args.poll_base_url:
        POLL_BASE_URL = args.poll_base_url.strip()
    if args.poll_api_path:
        POLL_API_PATH = args.poll_api_path.strip()
    if args.poll_pay_info_id:
        POLL_PAY_INFO_ID = args.poll_pay_info_id.strip()
    if args.poll_private_key:
        POLL_PRIVATE_KEY = args.poll_private_key.strip()
    if args.poll_limit is not None:
        POLL_LIMIT = args.poll_limit
    if args.poll_wait_seconds is not None:
        POLL_WAIT_SECONDS = args.poll_wait_seconds
    if args.poll_timeout_sec is not None:
        POLL_TIMEOUT_SEC = args.poll_timeout_sec
    if args.poll_interval_sec is not None:
        POLL_INTERVAL_SEC = args.poll_interval_sec
    if args.poll_trigger_sleep_sec is not None:
        POLL_TRIGGER_SLEEP_SEC = args.poll_trigger_sleep_sec
    if args.poll_state_file:
        POLL_STATE_FILE = args.poll_state_file

    # 上报未单独传参时，默认复用轮询配置，减少重复输入。
    if not REPORT_BASE_URL and POLL_BASE_URL:
        REPORT_BASE_URL = POLL_BASE_URL
    if not REPORT_PAY_INFO_ID and POLL_PAY_INFO_ID:
        REPORT_PAY_INFO_ID = POLL_PAY_INFO_ID
    if not REPORT_PRIVATE_KEY and POLL_PRIVATE_KEY:
        REPORT_PRIVATE_KEY = POLL_PRIVATE_KEY

    if REPORT_ENABLED and (not REPORT_BASE_URL or not REPORT_PAY_INFO_ID or not REPORT_PRIVATE_KEY):
        raise RuntimeError("缺少上报敏感参数，请通过命令行参数或环境变量传入 REPORT_BASE_URL/REPORT_PAY_INFO_ID/REPORT_PRIVATE_KEY")
    if POLL_ENABLED and (not POLL_BASE_URL or not POLL_PAY_INFO_ID or not POLL_PRIVATE_KEY):
        raise RuntimeError("缺少轮询敏感参数，请通过命令行参数或环境变量传入 POLL_BASE_URL/POLL_PAY_INFO_ID/POLL_PRIVATE_KEY")


def run(cmd, *, capture=True):
    return subprocess.run(
        cmd,
        check=True,
        text=True,
        capture_output=capture,
    )


def find_window():
    res = run(["xdotool", "search", "--name", APP_NAME])
    lines = [x.strip() for x in res.stdout.splitlines() if x.strip()]
    return lines[-1] if lines else ""


def get_geometry(wid):
    res = run(["xwininfo", "-id", wid])
    text = res.stdout

    def pick(pattern):
        m = re.search(pattern, text)
        return int(m.group(1)) if m else None

    abs_x = pick(r"Absolute upper-left X:\s+(-?\d+)")
    abs_y = pick(r"Absolute upper-left Y:\s+(-?\d+)")
    win_w = pick(r"Width:\s+(\d+)")
    win_h = pick(r"Height:\s+(\d+)")

    if None in (abs_x, abs_y, win_w, win_h):
        raise RuntimeError("无法解析窗口几何信息")
    return abs_x, abs_y, win_w, win_h


def scale_x(x, win_w):
    return int(x * win_w / BASE_W)


def scale_y(y, win_h):
    return int(y * win_h / BASE_H)


def get_mouse_position():
    res = run(["xdotool", "getmouselocation", "--shell"])
    x = re.search(r"^X=(\d+)$", res.stdout, re.M)
    y = re.search(r"^Y=(\d+)$", res.stdout, re.M)
    if not x or not y:
        raise RuntimeError("无法获取鼠标位置")
    return int(x.group(1)), int(y.group(1))


def human_move_and_click(target_x, target_y):
    try:
        start_x, start_y = get_mouse_position()
    except Exception:
        start_x, start_y = target_x, target_y

    # 分段移动并加入轻微抖动，避免机械式直达点击。
    steps = random.randint(8, 16)
    for i in range(1, steps + 1):
        t = i / steps
        ease = t * t * (3 - 2 * t)
        jitter_x = random.randint(-2, 2) if i < steps else 0
        jitter_y = random.randint(-2, 2) if i < steps else 0
        x = round(start_x + (target_x - start_x) * ease + jitter_x)
        y = round(start_y + (target_y - start_y) * ease + jitter_y)
        run(["xdotool", "mousemove", str(x), str(y)], capture=False)
        time.sleep(random.uniform(0.01, 0.04))

    time.sleep(random.uniform(0.03, 0.08))
    run(["xdotool", "mousedown", "1"], capture=False)
    time.sleep(random.uniform(0.03, 0.10))
    run(["xdotool", "mouseup", "1"], capture=False)


def click_rel(rx, ry, abs_x, abs_y, win_w, win_h):
    sx = scale_x(rx, win_w)
    sy = scale_y(ry, win_h)
    target_x = abs_x + sx
    target_y = abs_y + sy
    human_move_and_click(target_x, target_y)


def refresh_window(*, timeout=8.0, interval=0.2):
    # 小程序重载后可能重建窗口，循环重查可避免使用失效窗口 ID。
    deadline = time.time() + timeout
    last_err = None
    while time.time() < deadline:
        wid = find_window()
        if not wid:
            time.sleep(interval)
            continue

        try:
            run(["xdotool", "windowactivate", "--sync", wid], capture=False)
            abs_x, abs_y, win_w, win_h = get_geometry(wid)
            return wid, abs_x, abs_y, win_w, win_h
        except subprocess.CalledProcessError as e:
            last_err = e
            time.sleep(interval)

    if last_err:
        raise RuntimeError(f"无法激活或读取窗口信息: {last_err}") from last_err
    raise RuntimeError(f"未找到窗口: {APP_NAME}")


def capture_window(wid, out_img):
    run(["import", "-window", wid, out_img], capture=False)


def wait_file(path):
    p = Path(path)
    for _ in range(21):
        if p.exists() and p.stat().st_size > 0:
            return
        time.sleep(0.2)
    raise RuntimeError(f"截图失败: {path}")


def append_log(lines):
    # 统一追加日志，便于对照“截图 -> OCR 原文 -> 结构化结果”。
    log_path = Path(LOG_FILE)
    log_path.parent.mkdir(parents=True, exist_ok=True)
    with log_path.open("a", encoding="utf-8") as f:
        for line in lines:
            f.write(f"{line}\n")


def md5_upper(text):
    return hashlib.md5(text.encode("utf-8")).hexdigest().upper()


def build_sign(payload, private_key):
    # 服务端同款规则：按 key 升序拼接，空值不参与。
    items = []
    for k in sorted(payload.keys()):
        v = payload[k]
        if v is None:
            continue
        value = str(v)
        if not value.strip():
            continue
        # 与后端一致：仅判断空白是否为空，不做 trim 后再签名。
        items.append(f"{k}={value}")
    sign_text = "&".join(items) + f"&key={private_key}"
    return md5_upper(sign_text)


def normalize_amount_text(amount_text):
    s = str(amount_text or "").strip()
    if not s:
        return ""
    try:
        return Decimal(s).normalize().to_eng_string()
    except (InvalidOperation, ValueError):
        # 兜底：仅移除不必要的末尾 0（必须含小数点）。
        if re.fullmatch(r"\d+\.\d+", s):
            s = s.rstrip("0").rstrip(".")
        return s


def calc_crop_top_px(win_h):
    # 默认按“截图绝对像素”裁剪，避免重复缩放导致切多。
    if LIST_TOP_EXCLUDE_MODE in ("scaled", "scale", "relative"):
        return scale_y(LIST_TOP_EXCLUDE_Y, win_h)
    return LIST_TOP_EXCLUDE_Y


def crop_list_area(src_img_path, dst_img_path, top_exclude_px):
    src = Path(src_img_path)
    dst = Path(dst_img_path)
    if not src.exists():
        raise RuntimeError(f"image not found: {src}")

    if top_exclude_px <= 0:
        shutil.copy2(src, dst)
        return

    if shutil.which("convert") is None or shutil.which("identify") is None:
        # 无 ImageMagick 时降级为不裁剪，流程继续执行。
        print("未安装 imagemagick，跳过顶部裁剪。")
        shutil.copy2(src, dst)
        return

    size_text = subprocess.check_output(
        ["identify", "-format", "%w %h", str(src)],
        text=True,
        errors="ignore",
    ).strip()
    parts = size_text.split()
    if len(parts) != 2:
        shutil.copy2(src, dst)
        return

    img_w = int(parts[0])
    img_h = int(parts[1])
    if top_exclude_px >= img_h:
        raise RuntimeError(f"顶部排除高度过大: {top_exclude_px} >= {img_h}")

    crop_h = img_h - top_exclude_px
    run(
        [
            "convert",
            str(src),
            "-crop",
            f"{img_w}x{crop_h}+0+{top_exclude_px}",
            "+repage",
            str(dst),
        ],
        capture=False,
    )


def crop_vertical_band(src_img_path, dst_img_path, top_y, bottom_y):
    src = Path(src_img_path)
    dst = Path(dst_img_path)
    if not src.exists():
        raise RuntimeError(f"image not found: {src}")

    if shutil.which("convert") is None or shutil.which("identify") is None:
        print("未安装 imagemagick，跳过详情页带状裁剪。")
        shutil.copy2(src, dst)
        return

    size_text = subprocess.check_output(
        ["identify", "-format", "%w %h", str(src)],
        text=True,
        errors="ignore",
    ).strip()
    parts = size_text.split()
    if len(parts) != 2:
        shutil.copy2(src, dst)
        return

    img_w = int(parts[0])
    img_h = int(parts[1])
    top = max(0, min(img_h - 1, top_y))
    bottom = max(top + 1, min(img_h, bottom_y))
    crop_h = bottom - top
    run(
        [
            "convert",
            str(src),
            "-crop",
            f"{img_w}x{crop_h}+0+{top}",
            "+repage",
            str(dst),
        ],
        capture=False,
    )


def upscale_image(src_img_path, dst_img_path, percent):
    src = Path(src_img_path)
    dst = Path(dst_img_path)
    if not src.exists():
        raise RuntimeError(f"image not found: {src}")

    if shutil.which("convert") is None:
        print("未安装 imagemagick，跳过详情页放大。")
        shutil.copy2(src, dst)
        return

    p = max(100, int(percent))
    run(
        [
            "convert",
            str(src),
            "-resize",
            f"{p}%",
            str(dst),
        ],
        capture=False,
    )


def ocr_text(img_path):
    img = Path(img_path)
    if not img.exists():
        raise RuntimeError(f"image not found: {img}")

    if shutil.which("tesseract") is None:
        raise RuntimeError("未安装 tesseract，无法自动 OCR。")

    primary_cmd = ["tesseract", str(img), "stdout", "-l", OCR_LANG, "--psm", "6"]
    try:
        return subprocess.check_output(primary_cmd, text=True, errors="ignore")
    except subprocess.CalledProcessError:
        # 中文语言包缺失等场景下，回退到英文模型保证流程可用。
        fallback_cmd = ["tesseract", str(img), "stdout", "-l", "eng", "--psm", "6"]
        return subprocess.check_output(fallback_cmd, text=True, errors="ignore")


def ocr_tsv(img_path):
    img = Path(img_path)
    if not img.exists():
        raise RuntimeError(f"image not found: {img}")

    if shutil.which("tesseract") is None:
        raise RuntimeError("未安装 tesseract，无法自动 OCR。")

    primary_cmd = ["tesseract", str(img), "stdout", "-l", OCR_LANG, "--psm", "6", "tsv"]
    proc = subprocess.run(
        primary_cmd,
        text=True,
        errors="ignore",
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )
    if proc.returncode == 0:
        return proc.stdout

    fallback_cmd = ["tesseract", str(img), "stdout", "-l", "eng", "--psm", "6", "tsv"]
    proc2 = subprocess.run(
        fallback_cmd,
        text=True,
        errors="ignore",
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )
    if proc2.returncode == 0:
        return proc2.stdout

    return ""


def get_image_size(path):
    # 优先使用 Pillow 读取尺寸；不可用时回退到 ImageMagick。
    if Image is not None:
        try:
            with Image.open(str(path)) as img:
                return img.size
        except Exception:
            pass
    if shutil.which("identify") is None:
        return None, None
    size_text = subprocess.check_output(
        ["identify", "-format", "%w %h", str(path)],
        text=True,
        errors="ignore",
    ).strip()
    parts = size_text.split()
    if len(parts) != 2:
        return None, None
    return int(parts[0]), int(parts[1])


def extract_line_boxes_from_tsv(tsv_text):
    lines = [x for x in tsv_text.splitlines() if x.strip()]
    if len(lines) <= 1:
        return []

    rows = []
    for line in lines[1:]:
        cols = line.split("\t")
        if len(cols) < 12:
            continue
        try:
            level = int(cols[0])
            block_num = int(cols[2])
            par_num = int(cols[3])
            line_num = int(cols[4])
            left = int(cols[6])
            top = int(cols[7])
            width = int(cols[8])
            height = int(cols[9])
        except ValueError:
            continue
        text = cols[11].strip()
        rows.append((level, block_num, par_num, line_num, left, top, width, height, text))

    line_words = {}
    for row in rows:
        level, block_num, par_num, line_num, left, top, width, height, text = row
        if level != 5 or not text:
            continue
        key = (block_num, par_num, line_num)
        if key not in line_words:
            line_words[key] = {
                "left": left,
                "top": top,
                "right": left + width,
                "bottom": top + height,
                "words": [text],
            }
        else:
            obj = line_words[key]
            obj["left"] = min(obj["left"], left)
            obj["top"] = min(obj["top"], top)
            obj["right"] = max(obj["right"], left + width)
            obj["bottom"] = max(obj["bottom"], top + height)
            obj["words"].append(text)

    merged = []
    for _, obj in sorted(line_words.items(), key=lambda x: (x[0][0], x[0][1], x[0][2])):
        text = " ".join(obj["words"]).strip()
        left = obj["left"]
        top = obj["top"]
        width = max(1, obj["right"] - obj["left"])
        height = max(1, obj["bottom"] - obj["top"])
        merged.append(
            {
                "text": re.sub(r"\s+", " ", text),
                "compact": re.sub(r"\s+", "", text),
                "left": left,
                "top": top,
                "width": width,
                "height": height,
                "center_x": left + width // 2,
                "center_y": top + height // 2,
            }
        )
    merged.sort(key=lambda x: x["top"])
    return merged


def detect_list_row_segments(img_path):
    # 基于像素深浅估算列表记录所在的纵向区间，规避 OCR 行框缺失时的误点。
    if Image is None:
        return []

    try:
        with Image.open(str(img_path)) as img:
            gray = img.convert("L")
            img_w, img_h = gray.size
            pixels = gray.load()
    except Exception:
        return []

    dark_threshold = 240
    min_dark_pixels = max(12, img_w // 25)
    min_segment_height = max(8, img_h // 60)
    row_dark_counts = []
    for y in range(img_h):
        dark_count = 0
        for x in range(img_w):
            if pixels[x, y] < dark_threshold:
                dark_count += 1
        row_dark_counts.append(dark_count)

    segments = []
    start_y = None
    max_dark_count = 0
    for y, dark_count in enumerate(row_dark_counts):
        if dark_count >= min_dark_pixels:
            if start_y is None:
                start_y = y
                max_dark_count = dark_count
            else:
                max_dark_count = max(max_dark_count, dark_count)
            continue

        if start_y is None:
            continue

        end_y = y - 1
        if end_y - start_y + 1 >= min_segment_height and max_dark_count >= min_dark_pixels * 2:
            segments.append((start_y, end_y))
        start_y = None
        max_dark_count = 0

    if start_y is not None:
        end_y = img_h - 1
        if end_y - start_y + 1 >= min_segment_height and max_dark_count >= min_dark_pixels * 2:
            segments.append((start_y, end_y))

    return segments


def build_fallback_click_positions(img_path, entry_count, fallback_w, fallback_h):
    # 在 OCR 行框缺失时，优先按截图中的真实内容区间生成点击点。
    if entry_count <= 0:
        return []

    positions = []
    segments = detect_list_row_segments(img_path)
    if len(segments) >= entry_count:
        for top_y, bottom_y in segments[:entry_count]:
            seg_h = max(1, bottom_y - top_y + 1)
            click_y = top_y + min(seg_h - 1, max(6, int(seg_h * 0.45)))
            positions.append(
                {
                    "click_x": fallback_w // 2,
                    "click_y": click_y,
                    "click_source": "pixel_segment",
                    "segment_top": top_y,
                    "segment_bottom": bottom_y,
                }
            )
        return positions

    # 最后兜底仍采用均分，但点击点上移到分段上半区，避免落到下一条。
    span = max(1, fallback_h // (entry_count + 1))
    for idx in range(entry_count):
        click_y = int((idx + 0.5) * span)
        positions.append(
            {
                "click_x": fallback_w // 2,
                "click_y": max(5, min(fallback_h - 5, click_y)),
                "click_source": "uniform_upper_half",
            }
        )
    return positions


def clean_payer_name(raw_line):
    # 去掉金额和货币符号，仅保留付款人文本。
    s = re.sub(r"[¥￥Y]?\s*[0-9]+\.[0-9]{2}", "", raw_line)
    s = re.sub(r"\s+", " ", s).strip()
    # 去掉开头常见 OCR 噪声字符（引号、符号、单字母等）。
    s = re.sub(r"^[^0-9A-Za-z\u4e00-\u9fff]+", "", s)
    s = re.sub(r"^[A-Za-z]\s+", "", s)
    s = re.sub(r"\s+", " ", s).strip()
    if not s:
        return "未识别"

    tokens = [x for x in s.split(" ") if x]

    def normalize_name(candidate):
        name = re.sub(r"[^\u4e00-\u9fff]", "", candidate)
        if len(name) < 2:
            return "未识别"
        if len(name) <= 3:
            return name
        if len(name) == 4 and any(name.startswith(x) for x in COMPOUND_SURNAMES):
            return name
        # 4 字及以上默认按“尾部 3 字”修正，规避 OCR 前缀噪声。
        if len(name) >= 4:
            return name[-3:]
        # 理论不会走到这里，仅作兜底。
        return name

    # 若全是单个中文字符，优先按“姓名模板”修正。
    if tokens and all(re.fullmatch(r"[\u4e00-\u9fff]", t) for t in tokens):
        merged = "".join(tokens)
        if len(tokens) >= 4 and merged[0] not in COMMON_SURNAMES:
            return normalize_name(merged[1:])
        return normalize_name(merged)

    # 其余情况提取最后一段连续中文，通常最接近姓名区域。
    compact = re.sub(r"\s+", "", s)
    runs = re.findall(r"[\u4e00-\u9fff]{2,8}", compact)
    if runs:
        return normalize_name(runs[-1])

    return "未识别"


def extract_note_from_line(raw_line):
    line = raw_line.strip()
    if not line:
        return ""

    # 兼容 OCR 把“付款方备注”拆开或轻微错字（如“付 敦 方 备 注”）。
    marker_pat = re.compile(r"付\s*[\u4e00-\u9fff]?\s*方?\s*备\s*注|备\s*注")
    m = marker_pat.search(line)
    if not m:
        return ""

    tail = line[m.end() :]
    tail = re.sub(r"^[\s:：;；,，\-—]+", "", tail).strip()
    if not tail or tail in ("无", "暂无", "无备注", "-", "—"):
        return ""
    return tail


def extract_payment_list(img_path, *, win_w=None, win_h=None):
    # 识别列表记录，并为每条记录计算尽量稳定的点击坐标。
    run_ts = time.strftime("%Y-%m-%d %H:%M:%S")
    text = ocr_text(img_path)
    try:
        tsv_text = ocr_tsv(img_path)
        line_boxes = extract_line_boxes_from_tsv(tsv_text)
    except Exception:
        line_boxes = []

    print("OCR 原始识别文本:")
    print(text.strip() or "(空)")
    append_log(
        [
            "=" * 80,
            f"时间: {run_ts}",
            f"裁剪图片: {Path(img_path).resolve()}",
            "OCR 原始识别文本:",
            text.strip() or "(空)",
        ]
    )
    lines = [re.sub(r"\s+", " ", x).strip() for x in text.splitlines() if x.strip()]
    entries = []
    key_to_idx = {}
    time_pat = re.compile(
        r"(20\d{2}[-/.]\d{1,2}[-/.]\d{1,2}\s+\d{1,2}[:：]\d{1,2}(?::\d{1,2})?)|(\d{1,2}[:：]\d{1,2}(?::\d{1,2})?)"
    )
    amount_pat = re.compile(r"[¥￥Y]?\s*([0-9]+\.[0-9]{2})")

    for i, line in enumerate(lines):
        amount_m = amount_pat.search(line)
        if not amount_m:
            continue

        # 时间优先取当前行；没有则取后续 1~2 行。
        time_m = time_pat.search(line)
        time_line_idx = i
        if not time_m:
            for j in range(i + 1, min(i + 3, len(lines))):
                time_m = time_pat.search(lines[j])
                if time_m:
                    time_line_idx = j
                    break
        payer = clean_payer_name(line)
        amount = amount_m.group(1)
        pay_time = time_m.group(0).replace("：", ":") if time_m else ""
        # 时间 OCR 常漏识别：无时间时仍保留该条，按行号避免误去重。
        key = (payer, amount, pay_time) if pay_time else (payer, amount, f"line:{i}")
        note = ""

        if key in key_to_idx:
            idx = key_to_idx[key]
            if not entries[idx]["note"]:
                entries[idx]["note"] = note
            continue

        key_to_idx[key] = len(entries)
        entries.append(
            {
                "payer": payer,
                "amount": amount,
                "pay_time": pay_time,
                "amount_line_idx": i,
                "time_line_idx": time_line_idx,
                "note": note,
            }
        )

    # 按每条记录在 OCR 文本中的区间补提取备注。
    for idx, entry in enumerate(entries):
        start = entry["time_line_idx"]
        end = entries[idx + 1]["amount_line_idx"] if idx + 1 < len(entries) else len(lines)
        for k in range(start, end):
            note = extract_note_from_line(lines[k])
            if note:
                entry["note"] = note
                break

    # 将交易行映射到 OCR 行框，记录点击坐标（相对裁剪图）。
    prev_y = -1
    used_box_idx = set()
    for entry in entries:
        amount_num = entry["amount"].replace("¥", "")
        payer_compact = re.sub(r"\s+", "", entry["payer"])
        candidates = []
        for box_idx, box in enumerate(line_boxes):
            if box_idx in used_box_idx:
                continue
            if amount_num not in box["compact"]:
                continue
            score = 0
            if payer_compact and payer_compact in box["compact"]:
                score += 2
            if box["center_y"] > prev_y:
                score += 1
            candidates.append((score, box_idx, box))

        chosen = None
        if candidates:
            candidates.sort(key=lambda x: (-x[0], x[2]["top"]))
            chosen = candidates[0][2]
            used_box_idx.add(candidates[0][1])
            prev_y = max(prev_y, chosen["center_y"])

        entry["click_x"] = chosen["center_x"] if chosen else None
        entry["click_y"] = chosen["center_y"] if chosen else None
        entry["click_source"] = "ocr_box" if chosen else ""

    img_w, img_h = get_image_size(img_path)
    fallback_w = img_w or win_w or BASE_W
    fallback_h = img_h or win_h or BASE_H
    if entries:
        fallback_positions = build_fallback_click_positions(img_path, len(entries), fallback_w, fallback_h)
        for idx, entry in enumerate(entries):
            fallback_pos = fallback_positions[idx] if idx < len(fallback_positions) else None
            if entry["click_x"] is None:
                entry["click_x"] = fallback_pos["click_x"] if fallback_pos else fallback_w // 2
            if entry["click_y"] is None:
                entry["click_y"] = fallback_pos["click_y"] if fallback_pos else max(5, min(fallback_h - 5, (idx + 1) * 40))
            if not entry.get("click_source"):
                entry["click_source"] = (fallback_pos or {}).get("click_source", "fallback_default")
            if fallback_pos and fallback_pos.get("segment_top") is not None:
                entry["segment_top"] = fallback_pos.get("segment_top")
                entry["segment_bottom"] = fallback_pos.get("segment_bottom")

    if not entries:
        print("未从截图中识别到付款列表记录。")
        append_log(["结构化识别结果: 未识别到付款列表记录", f"日志文件: {Path(LOG_FILE).resolve()}"])
        return []

    print(f"识别到 {len(entries)} 条付款记录:")
    append_log([f"结构化识别结果: 共 {len(entries)} 条"])
    for idx, entry in enumerate(entries, start=1):
        note_text = f" | 付款方备注: {entry['note']}" if entry["note"] else ""
        click_source_text = f" | 点击来源: {entry.get('click_source') or 'unknown'}"
        segment_text = ""
        if entry.get("segment_top") is not None and entry.get("segment_bottom") is not None:
            segment_text = f" | 区间: ({entry['segment_top']}, {entry['segment_bottom']})"
        line = (
            f"{idx}. 付款人: {entry['payer']} | 金额: {entry['amount']} | 付款时间: {entry['pay_time']}"
            f"{note_text} | 单元坐标: ({entry['click_x']}, {entry['click_y']}){click_source_text}{segment_text}"
        )
        print(line)
        append_log([line])
    append_log([f"日志文件: {Path(LOG_FILE).resolve()}"])
    return entries


def extract_detail_fields(img_path):
    text = ocr_text(img_path)
    print("详情页 OCR 原始识别文本:")
    print(text.strip() or "(空)")
    append_log(
        [
            "详情页 OCR 原始识别文本:",
            text.strip() or "(空)",
        ]
    )

    lines = [re.sub(r"\s+", " ", x).strip() for x in text.splitlines() if x.strip()]
    compact_lines = [re.sub(r"\s+", "", x) for x in lines]
    detail = {"receive_time": "", "order_no": "", "payer_note": "", "pay_amount": ""}

    dt_pat = re.compile(r"20\d{2}[-/.]\d{1,2}[-/.]\d{1,2}\s+\d{1,2}:\d{2}(?::\d{2})?")
    order_inline_pat = re.compile(r"收\s*[\u4e00-\u9fff]?\s*单\s*号\s*[:：]?\s*([A-Za-z0-9\-]{6,})")
    amount_pat = re.compile(r"[¥￥]?\s*([0-9]+\.[0-9]{2})")

    def normalize_digits_only(s):
        return re.sub(r"\D+", "", s)

    def pick_longest_order_id(candidates):
        if not candidates:
            return ""
        candidates.sort(key=len, reverse=True)
        return candidates[0]

    def normalize_datetime(line):
        compact = re.sub(r"\s+", "", line)
        # 支持 OCR 将 HH:MM:SS 识别为 HHMM:SS
        m = re.search(r"(20\d{2}[-/.]\d{1,2}[-/.]\d{1,2})\s*(\d{4}:\d{2})", line)
        if m:
            d = m.group(1).replace("/", "-").replace(".", "-")
            hhmm_ss = m.group(2)
            return f"{d} {hhmm_ss[:2]}:{hhmm_ss[2:]}"

        m = re.search(r"(20\d{2}[-/.]\d{1,2}[-/.]\d{1,2})\s*(\d{1,2}:\d{2}(?::\d{2})?)", line)
        if m:
            d = m.group(1).replace("/", "-").replace(".", "-")
            t = m.group(2).replace("：", ":")
            return f"{d} {t}"

        m = re.search(r"(20\d{2}[-/.]\d{1,2}[-/.]\d{1,2})(\d{6})", compact)
        if m:
            d = m.group(1).replace("/", "-").replace(".", "-")
            t = m.group(2)
            return f"{d} {t[:2]}:{t[2:4]}:{t[4:]}"

        # 兼容 OCR 识别成“YYYY.MMDD HH:MM:SS”或“YYYY-MMDDHH:MM:SS”。
        m = re.search(r"(20\d{2})[-/.](\d{2})(\d{2})(\d{1,2}:\d{2}(?::\d{2})?)", compact)
        if m:
            yyyy = m.group(1)
            mm = m.group(2)
            dd = m.group(3)
            t = m.group(4).replace("：", ":")
            return f"{yyyy}-{mm}-{dd} {t}"

        # 兼容 OCR 识别成“YYYY.MMDDHHMMSS”。
        m = re.search(r"(20\d{2})[-/.](\d{2})(\d{2})(\d{6})", compact)
        if m:
            yyyy = m.group(1)
            mm = m.group(2)
            dd = m.group(3)
            t = m.group(4)
            return f"{yyyy}-{mm}-{dd} {t[:2]}:{t[2:4]}:{t[4:]}"
        return ""

    def is_noise_note_text(s):
        if not s:
            return True
        s = s.strip()
        if not s:
            return True
        if s in ("无", "暂无", "无备注", "-", "—", "--", "null", "None"):
            return True
        if re.fullmatch(r"[A-Za-z]$", s):
            return True
        if re.fullmatch(r"[\W_]+", s):
            return True
        compact = re.sub(r"\s+", "", s)
        if compact in ("备注", "付款备注", "付款方备注", "备志", "备註"):
            return True
        if compact.startswith("[") and ("发起" in compact or "可" in compact):
            return True
        return False

    for i, line in enumerate(lines):
        m = re.search(r"收款时间\s*[:：]?\s*(.+)$", line)
        if m:
            fixed = normalize_datetime(m.group(1))
            if fixed:
                detail["receive_time"] = fixed
                break
            # 标签后换行场景
            if i + 1 < len(lines):
                fixed = normalize_datetime(lines[i + 1])
                if fixed:
                    detail["receive_time"] = fixed
                    break
    if not detail["receive_time"]:
        # OCR 误字场景，时间行常出现“收款时间”附近关键词但字形错误。
        for line in lines:
            if "¥" in line or "￥" in line:
                continue
            if re.search(r"收|款|时|间|合", line):
                fixed = normalize_datetime(line)
                if fixed:
                    detail["receive_time"] = fixed
                    break
    if not detail["receive_time"]:
        for line in lines:
            if "¥" in line or "￥" in line:
                continue
            fixed = normalize_datetime(line)
            if fixed:
                detail["receive_time"] = fixed
                break

    order_candidates = []
    for i, line in enumerate(lines):
        m = order_inline_pat.search(line)
        if m:
            order_candidates.append(re.sub(r"[^A-Za-z0-9\-]", "", m.group(1)))

        compact = compact_lines[i]
        # OCR 误字场景：收歇单号 / 收款単号 / 收x单号等，只要含“收*单号”均视为单号行。
        if re.search(r"收[\u4e00-\u9fffA-Za-z0-9]{0,3}单号", compact) or "单号" in compact:
            tail = re.sub(r".*单号[:：]?", "", compact)
            tail_digits = normalize_digits_only(tail)
            if len(tail_digits) >= 10:
                order_candidates.append(tail_digits)

            for j in range(max(0, i - 1), min(len(lines), i + 2)):
                near_digits = normalize_digits_only(lines[j])
                if len(near_digits) >= 10:
                    order_candidates.append(near_digits)

            # 防止“单号文本下一行才是号码”。
            if i + 1 < len(lines):
                nxt_digits = normalize_digits_only(lines[i + 1])
                if len(nxt_digits) >= 10:
                    order_candidates.append(nxt_digits)

    # 兜底：全页找可能是单号的长数字（优先长度更长），但排除时间串。
    if not order_candidates:
        for line in lines:
            nums = re.findall(r"\d{10,}", line)
            for n in nums:
                if len(n) >= 10:
                    order_candidates.append(n)

    detail["order_no"] = pick_longest_order_id(order_candidates)

    # 详情金额通常位于详情区域顶部，优先提取第一个小数金额。
    for line in lines:
        m = amount_pat.search(line)
        if not m:
            continue
        detail["pay_amount"] = m.group(1)
        break

    note_candidates = []
    for i, line in enumerate(lines):
        compact = compact_lines[i]
        # 兼容“付款方备注/付款备注/备注”以及 OCR 误字“备志/备註”等。
        is_note_label = bool(
            re.search(r"(付款.{0,2}|付.{0,2}方)?备注|备志|备註", compact)
            or re.search(r"付.{0,2}方?备[注志註]", compact)
            or ("备" in compact and ("注" in compact or "志" in compact))
        )
        if not is_note_label:
            continue

        # 尝试从同一行取标签后的内容。
        line_nospace = re.sub(r"\s+", "", line)
        if re.match(r"^(付款.{0,2}|付.{0,2}方?)?(备注|备志|备註|备[注志註])[:：;；_]*", line_nospace):
            tail = re.sub(
                r"^(付款.{0,2}|付.{0,2}方?)?(备注|备志|备註|备[注志註])[:：;；_]*",
                "",
                line_nospace,
            ).strip()
            if not is_noise_note_text(tail):
                # 同行命中“标签+正文”可信度最高。
                note_candidates.append((5, tail))

        # 兜底：标签下一行/下两行是备注正文。
        for j in (i + 1, i + 2):
            if j >= len(lines):
                continue
            nxt = lines[j].strip()
            nxt_compact = compact_lines[j]
            if "单号" in nxt_compact or "时间" in nxt_compact or "记录" in nxt_compact:
                continue
            if "¥" in nxt or "￥" in nxt:
                continue
            if re.search(r"20\d{2}[-/.]\d{1,2}[-/.]\d{1,2}", nxt):
                continue
            if not is_noise_note_text(nxt):
                # 下1行优先级高于下2行。
                score = 2 if j == i + 1 else 1
                note_candidates.append((score, nxt))

    if note_candidates:
        note_candidates.sort(key=lambda x: x[0], reverse=True)
        detail["payer_note"] = note_candidates[0][1]

    return detail


def merge_detail_fields(primary, fallback):
    merged = dict(primary)
    for k in ("receive_time", "order_no", "payer_note", "pay_amount"):
        if not merged.get(k):
            merged[k] = fallback.get(k, "")
    return merged


def submit_reports(details):
    if not REPORT_ENABLED:
        print("已关闭上报: REPORT_ENABLED=0")
        append_log(["已关闭上报: REPORT_ENABLED=0"])
        return

    if not REPORT_PAY_INFO_ID or not REPORT_PRIVATE_KEY:
        msg = "上报跳过: 缺少 REPORT_PAY_INFO_ID 或 REPORT_PRIVATE_KEY"
        print(msg)
        append_log([msg])
        return

    pay_info_id = int(REPORT_PAY_INFO_ID) if REPORT_PAY_INFO_ID.isdigit() else REPORT_PAY_INFO_ID
    api_url = f"{REPORT_BASE_URL.rstrip('/')}/{REPORT_API_PATH.lstrip('/')}"
    print(f"开始上报，共 {len(details)} 条，接口: {api_url}")
    append_log([f"开始上报，共 {len(details)} 条，接口: {api_url}"])

    success = 0
    fail = 0
    skip = 0
    for item in details:
        pay_amount_raw = (item.get("detail_amount") or item.get("list_amount") or "").strip()
        pay_amount = normalize_amount_text(pay_amount_raw)
        payer_name = (item.get("list_payer") or "").strip()
        pay_time = (item.get("receive_time") or item.get("list_time") or "").strip()
        pay_order_no = (item.get("order_no") or "").strip()
        remark = (item.get("payer_note") or "").strip()

        # 关键字段缺失时跳过上报。
        if not (pay_amount and payer_name and pay_time and pay_order_no):
            skip += 1
            msg = (
                f"上报跳过 #{item.get('index')}: 关键信息缺失 "
                f"(amount={pay_amount or '-'}, payer={payer_name or '-'}, "
                f"time={pay_time or '-'}, orderNo={pay_order_no or '-'})"
            )
            print(msg)
            append_log([msg])
            continue

        body = {
            "payInfoId": pay_info_id,
            "payAmount": pay_amount,
            "payerName": payer_name,
            "payTime": pay_time,
            "payOrderNo": pay_order_no,
            "remark": remark,
            "signTimestamp": str(int(time.time() * 1000)),
            "signNonce": uuid.uuid4().hex[:16],
        }
        body["sign"] = build_sign(body, REPORT_PRIVATE_KEY)

        try:
            status_code, resp_text = post_json(api_url, body, REPORT_TIMEOUT_SEC)
            if 200 <= status_code < 300:
                success += 1
            else:
                fail += 1
            resp_text = (resp_text or "").strip()
            if len(resp_text) > 300:
                resp_text = resp_text[:300] + "...(truncated)"
            line = f"上报结果 #{item.get('index')}: HTTP {status_code} | {resp_text}"
            print(line)
            append_log([line])
        except Exception as e:
            fail += 1
            line = f"上报异常 #{item.get('index')}: {e}"
            print(line)
            append_log([line])

    summary = f"上报完成: 成功={success}, 失败={fail}, 跳过={skip}"
    print(summary)
    append_log([summary])


def post_json(api_url, body, timeout_sec):
    if requests is not None:
        resp = requests.post(
            api_url,
            json=body,
            headers={"Content-Type": "application/json"},
            timeout=timeout_sec,
        )
        return resp.status_code, resp.text

    data = json.dumps(body, ensure_ascii=False).encode("utf-8")
    req = urllib_request.Request(
        api_url,
        data=data,
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    try:
        with urllib_request.urlopen(req, timeout=timeout_sec) as resp:
            status = getattr(resp, "status", 200)
            text = resp.read().decode("utf-8", errors="ignore")
            return status, text
    except HTTPError as e:
        text = e.read().decode("utf-8", errors="ignore") if e.fp else str(e)
        return e.code, text
    except URLError as e:
        raise RuntimeError(str(e))


def get_json(api_url, params, timeout_sec):
    """
    发起 GET 请求并返回状态码与响应文本。

    :param api_url: 接口地址
    :param params: 查询参数
    :param timeout_sec: 超时时间
    :return: 状态码和响应文本
    """
    if requests is not None:
        resp = requests.get(
            api_url,
            params=params,
            timeout=timeout_sec,
        )
        return resp.status_code, resp.text

    query = urllib_parse.urlencode(params)
    req = urllib_request.Request(f"{api_url}?{query}", method="GET")
    try:
        with urllib_request.urlopen(req, timeout=timeout_sec) as resp:
            status = getattr(resp, "status", 200)
            text = resp.read().decode("utf-8", errors="ignore")
            return status, text
    except HTTPError as e:
        text = e.read().decode("utf-8", errors="ignore") if e.fp else str(e)
        return e.code, text
    except URLError as e:
        raise RuntimeError(str(e))


def load_poll_state():
    """
    读取轮询状态文件。

    :return: 状态字典
    """
    state_file = Path(POLL_STATE_FILE).expanduser()
    if not state_file.exists():
        return {"lastOrderId": 0}
    try:
        return json.loads(state_file.read_text(encoding="utf-8"))
    except Exception:
        return {"lastOrderId": 0}


def save_poll_state(last_order_id):
    """
    保存轮询状态文件。

    :param last_order_id: 最新游标
    :return: 无
    """
    state_file = Path(POLL_STATE_FILE).expanduser()
    state_file.parent.mkdir(parents=True, exist_ok=True)
    data = {
        "lastOrderId": int(last_order_id),
        "updatedAt": int(time.time()),
    }
    state_file.write_text(json.dumps(data, ensure_ascii=False, indent=2), encoding="utf-8")


def normalize_pay_info_id(raw_value):
    """
    规范化支付方式 ID。

    :param raw_value: 原始值
    :return: 整数或字符串
    """
    return int(raw_value) if str(raw_value).isdigit() else raw_value


def build_poll_params(last_order_id):
    """
    构建轮询请求参数。

    :param last_order_id: 当前游标
    :return: 参数字典
    """
    body = {
        "payInfoId": normalize_pay_info_id(POLL_PAY_INFO_ID),
        "lastOrderId": int(last_order_id),
        "limit": int(POLL_LIMIT),
        "waitSeconds": int(POLL_WAIT_SECONDS),
        "signTimestamp": str(int(time.time() * 1000)),
        "signNonce": uuid.uuid4().hex[:16],
    }
    body["sign"] = build_sign(body, POLL_PRIVATE_KEY)
    return body


def poll_new_orders(last_order_id):
    """
    调用长轮询接口获取新支付单。

    :param last_order_id: 当前游标
    :return: 轮询 payload
    """
    api_url = f"{POLL_BASE_URL.rstrip('/')}/{POLL_API_PATH.lstrip('/')}"
    params = build_poll_params(last_order_id)
    status_code, resp_text = get_json(api_url, params, POLL_TIMEOUT_SEC)
    if not (200 <= status_code < 300):
        raise RuntimeError(f"轮询失败: HTTP {status_code} | {resp_text}")

    try:
        data = json.loads(resp_text)
    except Exception as ex:
        raise RuntimeError(f"轮询返回不是合法 JSON: {resp_text}") from ex

    if isinstance(data, dict) and isinstance(data.get("payload"), dict):
        return data["payload"]
    return data


def click_list_entry(entry, abs_x, abs_y, win_w, win_h, crop_top_px, wid=None):
    # 优先使用窗口内坐标点击，避免窗口边框/标题栏导致的全局坐标偏移。
    rel_x = int(entry.get("click_x", win_w // 2))
    rel_y = int(entry.get("click_y", max(1, (win_h - crop_top_px) // 2)))
    rel_x = max(5, min(win_w - 5, rel_x))
    rel_y = max(5, min(max(6, win_h - crop_top_px - 5), rel_y))
    target_x = rel_x
    target_y = crop_top_px + rel_y

    if wid:
        target_x = max(5, min(win_w - 5, target_x))
        target_y = max(5, min(win_h - 5, target_y))
        run(["xdotool", "mousemove", "--window", str(wid), str(target_x), str(target_y)], capture=False)
        time.sleep(random.uniform(0.03, 0.08))
        run(["xdotool", "mousedown", "1"], capture=False)
        time.sleep(random.uniform(0.03, 0.10))
        run(["xdotool", "mouseup", "1"], capture=False)
        return

    # 兼容旧逻辑：缺少窗口 ID 时回退到全局坐标点击。
    human_move_and_click(abs_x + target_x, abs_y + target_y)


def run_query_and_extract_list():
    # 执行查询流程并返回窗口信息、裁剪偏移和列表识别结果。
    wid, abs_x, abs_y, win_w, win_h = refresh_window(timeout=3.0)

    # 先打开小程序菜单，再点击“重新加载小程序”
    click_rel(MINIPROGRAM_MENU_X, MINIPROGRAM_MENU_Y, abs_x, abs_y, win_w, win_h)
    time.sleep(1)
    click_rel(MINIPROGRAM_RELOAD_X, MINIPROGRAM_RELOAD_Y, abs_x, abs_y, win_w, win_h)
    time.sleep(1.0)
    wid, abs_x, abs_y, win_w, win_h = refresh_window(timeout=10.0)

    time.sleep(5.0)
    click_rel(RECEIPT_RECORD_X, RECEIPT_RECORD_Y, abs_x, abs_y, win_w, win_h)
    time.sleep(0.8)
    click_rel(CUSTOM_QUERY_X, CUSTOM_QUERY_Y, abs_x, abs_y, win_w, win_h)
    time.sleep(0.8)
    click_rel(LAST_24H_X, LAST_24H_Y, abs_x, abs_y, win_w, win_h)
    time.sleep(0.5)
    click_rel(QUERY_BUTTON_X, QUERY_BUTTON_Y, abs_x, abs_y, win_w, win_h)
    time.sleep(1.0)

    out = Path(OUT_IMG)
    out_list = Path(OUT_LIST_IMG)
    out.parent.mkdir(parents=True, exist_ok=True)
    out_list.parent.mkdir(parents=True, exist_ok=True)
    if out.exists():
        out.unlink()
    if out_list.exists():
        out_list.unlink()

    capture_window(wid, OUT_IMG)
    wait_file(OUT_IMG)
    crop_top_px = calc_crop_top_px(win_h)
    print(
        f"顶部裁剪: mode={LIST_TOP_EXCLUDE_MODE}, 配置={LIST_TOP_EXCLUDE_Y}px, 实际={crop_top_px}px, 窗口高={win_h}px"
    )
    append_log(
        [
            f"顶部裁剪参数: mode={LIST_TOP_EXCLUDE_MODE}, 配置={LIST_TOP_EXCLUDE_Y}px, 实际={crop_top_px}px, 窗口高={win_h}px"
        ]
    )
    crop_list_area(OUT_IMG, OUT_LIST_IMG, crop_top_px)
    wait_file(OUT_LIST_IMG)
    print(f"裁剪后图片: {Path(OUT_LIST_IMG).resolve()}")
    entries = extract_payment_list(OUT_LIST_IMG, win_w=win_w, win_h=max(1, win_h - crop_top_px))
    return wid, abs_x, abs_y, win_w, win_h, crop_top_px, entries


def execute_local_check_and_report():
    """
    执行一次本地 OCR 检查与支付上报。

    :return: 无
    """
    flow_start = time.perf_counter()
    try:
        wid, abs_x, abs_y, win_w, win_h, crop_top_px, first_entries = run_query_and_extract_list()
    except RuntimeError as e:
        print(str(e), file=sys.stderr)
        sys.exit(1)

    if not first_entries:
        print(f"日志已写入: {Path(LOG_FILE).resolve()}")
        elapsed = time.perf_counter() - flow_start
        print(f"流程总耗时: {elapsed:.2f} 秒")
        append_log([f"流程总耗时: {elapsed:.2f} 秒"])
        return

    details = []
    total = len(first_entries)
    for idx in range(total):
        # 为了速度只检查两条记录
        # todo 这里可以做cache对于成功上报的订单直接跳过
        if idx >=1:
            break

        if idx == 0:
            entries = first_entries
        else:
            print(f"处理第 {idx + 1}/{total} 条前，重新执行列表查询流程...")
            append_log([f"处理第 {idx + 1}/{total} 条前，重新执行列表查询流程..."])
            wid, abs_x, abs_y, win_w, win_h, crop_top_px, entries = run_query_and_extract_list()

        if idx >= len(entries):
            print(f"第 {idx + 1} 条在当前列表中不存在，跳过。")
            append_log([f"第 {idx + 1} 条在当前列表中不存在，跳过。"])
            continue

        entry = entries[idx]
        print(
            f"点击第 {idx + 1} 条记录: 付款人={entry['payer']}, 金额={entry['amount']}, 时间={entry['pay_time']}"
        )
        append_log(
            [
                (
                    f"点击第 {idx + 1} 条记录: 付款人={entry['payer']}, 金额={entry['amount']}, "
                    f"时间={entry['pay_time']}, 坐标=({entry['click_x']},{entry['click_y']})"
                )
            ]
        )
        click_list_entry(entry, abs_x, abs_y, win_w, win_h, crop_top_px, wid=wid)
        time.sleep(1.8)

        detail_img = LOG_DIR / f"skb_detail_page_{idx + 1}.png"
        detail_ocr_img = LOG_DIR / f"skb_detail_page_{idx + 1}_ocr.png"
        detail_ocr_upscaled_img = LOG_DIR / f"skb_detail_page_{idx + 1}_ocr_upscaled.png"
        if detail_img.exists():
            detail_img.unlink()
        if detail_ocr_img.exists():
            detail_ocr_img.unlink()
        if detail_ocr_upscaled_img.exists():
            detail_ocr_upscaled_img.unlink()
        capture_window(wid, str(detail_img))
        wait_file(str(detail_img))
        crop_vertical_band(str(detail_img), str(detail_ocr_img), DETAIL_OCR_TOP_Y, DETAIL_OCR_BOTTOM_Y)
        wait_file(str(detail_ocr_img))
        upscale_image(str(detail_ocr_img), str(detail_ocr_upscaled_img), DETAIL_OCR_UPSCALE_PERCENT)
        wait_file(str(detail_ocr_upscaled_img))
        print(
            (
                f"详情页裁剪+放大: top={DETAIL_OCR_TOP_Y}px, bottom={DETAIL_OCR_BOTTOM_Y}px, "
                f"upscale={DETAIL_OCR_UPSCALE_PERCENT}%, 图片={detail_ocr_upscaled_img.resolve()}"
            )
        )
        append_log(
            [
                (
                    f"详情页裁剪参数: top={DETAIL_OCR_TOP_Y}px, bottom={DETAIL_OCR_BOTTOM_Y}px, "
                    f"upscale={DETAIL_OCR_UPSCALE_PERCENT}%, 图片={detail_ocr_upscaled_img.resolve()}"
                )
            ]
        )
        detail = extract_detail_fields(str(detail_ocr_upscaled_img))
        if not detail.get("receive_time") or not detail.get("order_no"):
            append_log(["详情页放大图关键字段缺失，回退原裁剪图再次识别。"])
            detail_fallback = extract_detail_fields(str(detail_ocr_img))
            detail = merge_detail_fields(detail, detail_fallback)
        detail_record = {
            "index": idx + 1,
            "list_payer": entry["payer"],
            "list_amount": entry["amount"],
            "list_time": entry["pay_time"],
            "detail_amount": detail["pay_amount"],
            "receive_time": detail["receive_time"],
            "order_no": detail["order_no"],
            "payer_note": detail["payer_note"],
        }
        details.append(detail_record)
        print(
            (
                f"详情提取 {idx + 1}: 详情金额={detail_record['detail_amount'] or '未识别'} | "
                f"收款时间={detail_record['receive_time'] or '未识别'} | "
                f"收款单号={detail_record['order_no'] or '未识别'} | "
                f"付款方备注={detail_record['payer_note'] or '无'}"
            )
        )
        append_log(
            [
                (
                    f"详情提取 {idx + 1}: 详情金额={detail_record['detail_amount'] or '未识别'} | "
                    f"收款时间={detail_record['receive_time'] or '未识别'} | "
                    f"收款单号={detail_record['order_no'] or '未识别'} | "
                    f"付款方备注={detail_record['payer_note'] or '无'}"
                )
            ]
        )

    print(f"详情页结构化识别结果: 共 {len(details)} 条")
    append_log([f"详情页结构化识别结果: 共 {len(details)} 条"])
    for item in details:
        line = (
            f"{item['index']}. 列表付款人: {item['list_payer']} | 列表金额: {item['list_amount']} | "
            f"列表时间: {item['list_time']} | 详情金额: {item['detail_amount'] or '未识别'} | "
            f"收款时间: {item['receive_time'] or '未识别'} | "
            f"收款单号: {item['order_no'] or '未识别'} | 付款方备注: {item['payer_note'] or '无'}"
        )
        print(line)
        append_log([line])

    submit_reports(details)

    print(f"日志已写入: {Path(LOG_FILE).resolve()}")
    elapsed = time.perf_counter() - flow_start
    print(f"流程总耗时: {elapsed:.2f} 秒")
    append_log([f"流程总耗时: {elapsed:.2f} 秒"])


def run_poll_loop():
    """
    执行长轮询循环，并在收到新支付单后触发本地检查上报。

    :return: 无
    """
    while True:
        state = load_poll_state()
        last_order_id = int((state or {}).get("lastOrderId", 0) or 0)
        append_log([f"开始长轮询: lastOrderId={last_order_id}, waitSeconds={POLL_WAIT_SECONDS}, limit={POLL_LIMIT}"])
        payload = poll_new_orders(last_order_id)
        if not isinstance(payload, dict):
            raise RuntimeError(f"轮询返回结构错误: {payload}")

        success = bool(payload.get("success", False))
        message = str(payload.get("message", "") or "")
        orders = payload.get("orders") or []
        next_order_id = int(payload.get("nextOrderId", last_order_id) or last_order_id)

        append_log([f"轮询结果: success={success}, message={message}, count={len(orders)}, nextOrderId={next_order_id}"])
        if not success:
            raise RuntimeError(f"轮询业务失败: {message}")

        if not orders:
            print(f"暂无新支付单，nextOrderId={next_order_id}")
            time.sleep(POLL_INTERVAL_SEC)
            continue

        summary = []
        for order in orders[:5]:
            summary.append(
                f"id={order.get('id')},orderNo={order.get('orderNo')},amount={order.get('amount')},status={order.get('status')}"
            )
        if len(orders) > 5:
            summary.append(f"...共{len(orders)}条")
        summary_text = " | ".join(summary)

        print(f"收到新支付单 {len(orders)} 条，准备执行本地 OCR+上报: {summary_text}")
        append_log([f"收到新支付单 {len(orders)} 条: {summary_text}"])
        os.environ["POLL_TRIGGER_ORDERS_JSON"] = json.dumps(orders, ensure_ascii=False)
        os.environ["POLL_TRIGGER_NEXT_ORDER_ID"] = str(next_order_id)

        if POLL_TRIGGER_SLEEP_SEC > 0:
            print(f"检测到新支付单，等待 {POLL_TRIGGER_SLEEP_SEC:.0f} 秒后开始检查上报")
            append_log([f"检测到新支付单，等待 {POLL_TRIGGER_SLEEP_SEC:.0f} 秒后开始检查上报"])
            time.sleep(POLL_TRIGGER_SLEEP_SEC)

        execute_local_check_and_report()
        save_poll_state(next_order_id)
        append_log([f"本轮处理完成，游标已更新为 {next_order_id}"])
        time.sleep(POLL_INTERVAL_SEC)


def main():
    """
    程序主入口。

    :return: 无
    """
    args = parse_args()
    init_config(args)
    if POLL_ENABLED:
        run_poll_loop()
        return
    execute_local_check_and_report()


if __name__ == "__main__":
    main()
