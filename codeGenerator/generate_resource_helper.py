import requests
import os
import shutil
import json

from pathlib import Path

APP_PATH = Path(__file__).parent.absolute().parent
APP_CODE_PATH = APP_PATH / Path("app/src/main")

FLAGS_DIR = Path("flags")
NAMES_DIR = Path("names")

DRAWABLES_DIR = APP_CODE_PATH / Path("res/drawable-xhdpi")
RESOURCE_HELPER_PATH = APP_CODE_PATH / \
    Path("java/com/tibagni/covid/utils/ResourceHelper.kt")

COUNTRY_CODES = [
    "AF", "ZA", "AL", "DE", "AD", "AO", "AI", "AQ", "AG", "SA", "DZ", "AR", "AM",
    "AW", "AU", "AT", "AZ", "BS", "BH", "BD", "BB", "BE", "BZ", "BJ", "BM", "BY",
    "BO", "BA", "BW", "BR", "BN", "BG", "BF", "BI", "BT", "CV", "CM", "KH", "CA",
    "QA", "KZ", "TD", "CL", "CN", "CY", "VA", "CO", "KM", "CD", "KP", "KR", "CI",
    "CR", "HR", "CU", "CW", "DK", "DJ", "DM", "EG", "SV", "AE", "EC", "ER", "SK",
    "SI", "ES", "SZ", "US", "EE", "ET", "FJ", "PH", "FI", "FR", "GA", "GM", "GH",
    "GE", "GI", "GD", "GR", "GL", "GP", "GU", "GT", "GG", "GY", "GF", "GN", "GQ",
    "GW", "HT", "HN", "HK", "HU", "YE", "BV", "CX", "IM", "NF", "AX", "KY", "CC",
    "CK", "FO", "GS", "HM", "FK", "MP", "MH", "UM", "PN", "SB", "TC", "VI", "VG",
    "IN", "ID", "IR", "IQ", "IE", "IS", "IL", "IT", "JM", "JP", "JE", "JO", "KW",
    "LA", "LS", "LV", "LB", "LR", "LY", "LI", "LT", "LU", "MO", "MK", "MG", "MY",
    "MW", "MV", "ML", "MT", "MA", "MQ", "MU", "MR", "YT", "MX", "MM", "FM", "MZ",
    "MD", "MC", "MN", "ME", "MS", "NA", "NR", "NP", "NI", "NE", "NG", "NU", "NO",
    "NC", "NZ", "OM", "NL", "BQ", "PW", "PA", "PG", "PK", "PY", "PE", "PF", "PL",
    "PR", "PT", "KE", "KG", "KI", "GB", "CF", "CG", "DO", "RE", "RO", "RW", "RU",
    "EH", "WS", "AS", "SM", "SH", "LC", "BL", "KN", "MF", "PM", "ST", "VC", "SC",
    "SN", "SL", "RS", "SG", "SX", "SY", "SO", "LK", "SD", "SS", "SE", "CH", "SR",
    "SJ", "TJ", "TH", "TW", "TZ", "CZ", "IO", "TF", "PS", "TL", "TG", "TK", "TO",
    "TT", "TN", "TM", "TR", "TV", "UA", "UG", "UY", "UZ", "VU", "VE", "VN", "WF",
    "ZM", "ZW"
]


def list_existing_flags():
    existing_flags = os.listdir(FLAGS_DIR)
    existing_flags = [f for f in existing_flags if f.endswith("_flag.png")]
    return [name[:2] for name in existing_flags]


def download_flags():
    # Do not download again if it already exists
    existing_flags = list_existing_flags()
    to_download = [cc for cc in COUNTRY_CODES if cc not in existing_flags]

    download_count = 0
    print(
        f"Will download {len(to_download)} flags. {len(existing_flags)} already exist")
    for cc in to_download:
        download_count += 1
        progress = f"{download_count} of {len(to_download)}"
        print(f"Downloading flag for {cc} | {progress}...  ", end="\r")

        response = requests.get(
            f"https://www.countryflags.io/{cc}/flat/64.png")
        with open(FLAGS_DIR / f"{cc}_flag.png", "wb") as img:
            img.write(response.content)

    print(f"Finished downloading {download_count} flags!", " " * 20)


def copy_to_drawable():
    all_flags = os.listdir(FLAGS_DIR)
    all_flags = [Path(FLAGS_DIR / f) for f in all_flags]

    for flag in all_flags:
        shutil.copy(flag, DRAWABLES_DIR / flag.name.lower())


def create_string_resources():
    available_languages = os.listdir(NAMES_DIR)
    names_files = [NAMES_DIR /
                   f for f in available_languages if f.endswith(".json")]

    for names_file in names_files:
        with open(names_file) as f:
            names_dict = json.loads(f.read())

        string_declaration = '    <string name="{}_country_name">{}</string>'
        full_declaration = "\n".join([string_declaration.format(
            cc.lower(), names_dict[cc]) for cc in COUNTRY_CODES])
        full_declaration = full_declaration.replace("&", "&amp;")

        with open("strings_template") as template:
            string_resource_file = template.read()
            string_resource_file = string_resource_file.replace(
                "$$strings_declaration$$", full_declaration)

        modifier = "" if names_file.name.endswith(
            "default.json") else "-" + names_file.name[:names_file.name.index(".")]
        string_resource_path = APP_CODE_PATH / Path(f"res/values{modifier}")

        if not os.path.isdir(string_resource_path):
            os.mkdir(string_resource_path)

        with open(string_resource_path / "country_names.xml", "w") as res_file:
            res_file.write(string_resource_file)


def generate_resource_helper_class():
    get_flag_impl_lines = []
    get_name_impl_lines = []
    indent = " " * 4 * 3
    for cc in COUNTRY_CODES:
        get_flag_impl_lines.append(
            indent + f'"{cc.lower()}" -> R.drawable.{cc.lower()}_flag')
        
        get_name_impl_lines.append(
            indent + f'"{cc.lower()}" -> R.string.{cc.lower()}_country_name')

    get_flag_impl = "\n".join(get_flag_impl_lines)
    get_name_impl = "\n".join(get_name_impl_lines)

    with open("resource_helper_template") as template:
        resource_helper = template.read()
        resource_helper = resource_helper.replace(
            "$$get_flag_impl$$", get_flag_impl)
        resource_helper = resource_helper.replace(
            "$$get_name_impl$$", get_name_impl)

    with open(RESOURCE_HELPER_PATH, "w") as resource_helper_class:
        resource_helper_class.write(resource_helper)


def main():
    download_flags()
    copy_to_drawable()
    create_string_resources()
    generate_resource_helper_class()


if __name__ == "__main__":
    main()
