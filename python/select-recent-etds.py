#!/usr/bin/env python3
"""
Select and sort ETDs
"""
import os
import shutil
import xml.etree.ElementTree as ET
from pathlib import Path

__author__ = "William A. Ingram"
__version__ = "0.1.0"
__license__ = "BSD-3"


def select_latest_docs():
    """ Select ETDs from 2017 and 2018; there should be about 1000 of them """

    data = Path('../vtetds')
    theses = [t for t in (data / 'thesis').iterdir() if t.is_dir()]
    dissertations = [d for d in (data / 'dissertation').iterdir() if d.is_dir()]

    selected_thesis = data / 'thesis'
    selected_dissertation = data / 'dissertation'

    if not os.path.exists(selected_thesis.name):
        os.makedirs(selected_thesis.name)

    if not os.path.exists(selected_dissertation.name):
        os.makedirs(selected_dissertation.name)

    for d in dissertations:
        copy_to_dest(selected_dissertation, d)

    for t in theses:
        copy_to_dest(selected_thesis, t)


def copy_to_dest(destination_path, item_path):
    print("Processing file %s" % item_path.absolute())
    metadata_file = item_path / 'dublin_core.xml'
    tree = ET.parse(metadata_file.as_posix())
    doc = tree.getroot()
    date_issued = doc.find(".//dcvalue[@element='date'][@qualifier='issued']").text
    year = date_issued.split('-')[0]
    try:
        if year == '2018' or year == '2017':
            shutil.copytree(str(item_path), str(destination_path / item_path.name))
    except FileExistsError:
        pass


def sort_by_dept():
    """ Sort by department """

    data = Path('../vtetds')
    theses = [t for t in (data / 'thesis').iterdir() if t.is_dir()]
    dissertations = [d for d in (data / 'dissertation').iterdir() if d.is_dir()]

    for d in dissertations:
        make_dept_dir(d, data)

    for t in theses:
        make_dept_dir(t, data)


def make_dept_dir(item_path, root_path):
    print("Processing file %s" % item_path.absolute())
    metadata_file = item_path / 'dublin_core.xml'
    tree = ET.parse(metadata_file.as_posix())
    doc = tree.getroot()
    department = doc.find(".//dcvalue[@element='contributor'][@qualifier='department']").text
    department = department.replace('&#x20;', ' ')
    dept_dir = root_path / 'dissertation' / department
    if not dept_dir.exists():
        dept_dir.mkdir()
    shutil.move(str(item_path), str(dept_dir / item_path.name))


def reset_dept():
    data = Path('../vtetds')
    dissertations = [d for d in (data / 'dissertation').iterdir() if d.is_dir()]

    for d in dissertations:
        if not d.name.isdigit():
            children = [child for child in d.iterdir() if child.is_dir()]
            for child in children:
                shutil.move(str(child), str(data / 'dissertation'))


def main():
    select_latest_docs()


if __name__ == "__main__":
    """  """
    main()
