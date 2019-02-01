#!/usr/bin/env python3
# coding: utf-8

"""
ACMDL metadata from CSV to BibTex
"""

import requests
import json
from time import sleep

from crossref.restful import Works, Etiquette
from requests import Session
from requests import Request

__author__ = "William A. Ingram"
__version__ = "0.1.0"
__license__ = "BSD3"


def main():
    """  """
    acm_path = '/Users/waingram/Projects/acmdl/acm_refs.txt'
    bibtex_file = './acmdl.bib'

    dois = extract_dois(acm_path)

    print('Collected %d DOIs... now processing' % len(dois))

    write_bibtex_v2(bibtex_file, dois)


def extract_dois(acm_path):
    dois = set()
    with open(acm_path) as f:
        line = f.readline()
        while line:
            marker = line.find('doi')
            if marker != -1:
                doi = line[marker + 5:-3]
                if doi.startswith('10.'):
                    dois.add(doi)
            line = f.readline()
    return dois


def write_bibtex_v1(bibtex_file, dois):
    my_etiquette = Etiquette('VTLibraries', 0.1, 'https://lib.vt.edu/', 'waingram@vt.edu')
    work = Works(etiquette=my_etiquette)
    with open(bibtex_file, 'w') as bib:
        for doi in dois:
            url = "http://api.crossref.org/works/" + doi + "/transform/application/x-bibtex"
            jsontxt = work.do_http_request('get', url, custom_header=str(work.etiquette)).text
            if not jsontxt.startswith('Resource'):
                bib.writelines(jsontxt)
                bib.write('\n')


def write_bibtex_v2(bibtex_file, dois):

    with open(bibtex_file, 'w') as bib:
        for doi in dois:
            url = "https://doi.org/" + doi
            resp = requests.get(url, headers={'Accept': 'text/bibliography; style=bibtex'}, timeout=100)
            if resp.status_code == 200 and resp.headers['content-type'] == 'text/bibliography':
                bibtex = resp.content.decode('utf8', 'ignore')
                if bibtex:
                    bib.writelines(bibtex)
                    bib.write('\n')
                else:
                    continue
            else:
                continue


if __name__ == "__main__":
    """  """
    main()
