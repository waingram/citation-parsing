#!/usr/bin/env python3
"""
Count departments from ETD collection
"""
import os
import shutil
import xml.etree.ElementTree as ET
from pathlib import Path

__author__ = "William A. Ingram"
__version__ = "0.1.0"
__license__ = "BSD-3"


def count_depts():
    """ Sort by department """

    data = Path('.')
    theses = [t for t in (data / 'thesis').iterdir() if t.is_dir()]
    dissertations = [d for d in (data / 'dissertation').iterdir() if d.is_dir()]

    depts = set()

    for d in dissertations:
        depts.add(get_dept(d))

    for t in theses:
        depts.add(get_dept(t))

    print(depts)
    print(len(depts))


def get_dept(item_path):
    print("Processing file %s" % item_path.absolute())
    metadata_file = item_path / 'dublin_core.xml'
    tree = ET.parse(metadata_file.as_posix())
    doc = tree.getroot()
    try:
        department = doc.find(".//dcvalue[@element='contributor'][@qualifier='department']").text
        department = department.replace('&#x20;', ' ')
    except:
        department = None
    return department


def main():
    count_depts()


if __name__ == "__main__":
    """  """
    main()
