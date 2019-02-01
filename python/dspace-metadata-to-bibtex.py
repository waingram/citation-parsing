#!/usr/bin/env python3
"""
Convert DSpace metadata to BibTex
"""
import warnings
import xml.etree.ElementTree as ET
from pathlib import Path

from bibtexparser.bibdatabase import BibDatabase
from bibtexparser.bwriter import BibTexWriter

__author__ = "William A. Ingram"
__version__ = "0.1.0"
__license__ = "BSD3"


def extract_bibtex(item_path):
    bibtex = {}

    # print("Processing file %s" % item_path.absolute())

    thesis_metadata_file = item_path / 'metadata_thesis.xml'
    tree = ET.parse(thesis_metadata_file.as_posix())
    doc = tree.getroot()

    md_node = doc.find(".//dcvalue[@element='degree'][@qualifier='level']")
    if md_node is not None:
        md_value = md_node.text

        if md_value == 'masters' or md_value == 'undergraduate':
            bibtex['ENTRYTYPE'] = 'mastersthesis'
        elif md_value == 'doctoral':
            bibtex['ENTRYTYPE'] = 'phdthesis'
    else:
        warnings.warn('%s is not an ETD' % item_path)
        return bibtex

    metadata_file = item_path / 'dublin_core.xml'
    tree = ET.parse(metadata_file.as_posix())
    doc = tree.getroot()

    author_ln = ''
    md_node = doc.find(".//dcvalue[@element='contributor'][@qualifier='author']")
    if md_node is not None:
        md_value = md_node.text
        author_ln = md_value.split()[0].replace(',', '')
        bibtex['author'] = md_value

    title_fw = ''
    md_node = doc.find(".//dcvalue[@element='title']")
    if md_node is not None:
        md_value = md_node.text
        title_fw = md_value.split()[0]
        bibtex['title'] = md_value

    md_node = doc.find(".//dcvalue[@element='publisher']")
    if md_node is not None:
        md_value = md_node.text
        bibtex['school'] = md_value

    # here use 'URL' but could use 'howpublished' or something else
    md_node = doc.find(".//dcvalue[@element='identifier'][@qualifier='uri']")
    if md_node is not None:
        md_value = md_node.text
        bibtex['URL'] = md_value

    year = ''
    md_node = doc.find(".//dcvalue[@element='date'][@qualifier='issued']")
    if md_node is not None:
        md_value = md_node.text
        year = md_value.split('-')[0]
        bibtex['year'] = year

    bibtex['ID'] = (author_ln + year + title_fw).lower()

    return bibtex


def main():
    """  """
    path_files = '.'
    bibtex_file = './vtedts.bib'

    data = Path(path_files)

    theses = [t for t in (data / 'thesis').iterdir() if t.is_dir()]
    dissertations = [d for d in (data / 'dissertation').iterdir() if d.is_dir()]

    db = BibDatabase()

    for t in theses:
        db.entries.append(extract_bibtex(t))

    for d in dissertations:
        db.entries.append(extract_bibtex(d))

    writer = BibTexWriter()

    with open(bibtex_file, 'w') as bibfile:
        bibfile.write(writer.write(db))


if __name__ == "__main__":
    """  """
    main()
