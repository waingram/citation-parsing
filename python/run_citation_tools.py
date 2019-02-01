#!/usr/bin/env python3
"""
Run various citation parsers
"""
import os
import subprocess

__author__ = "William A. Ingram"
__version__ = "0.1.0"
__license__ = "BSD3"

CERMINE_COMMAND = "java -Xmx2G -cp cermine-impl-1.14-20180204.213009-17-jar-with-dependencies.jar " \
                  "pl.edu.icm.cermine.ContentExtractor -path ../vtetds/ -outputs bibtext "

GROBID_HOME = "/Users/waingram/Projects/grobid/"
GROBID_COMMAND = "java -Xmx1G -jar grobid-core/build/libs/grobid-core-0.5.2-SNAPSHOT-onejar.jar -gH grobid-home -exe " \
                 "processRawReference -s "


def main():
    """  """

    citation_string = 'Agre. P. E. and Chapman. D. (1987). PENGI: An implementation of a theory of activity. In Proceedings o f the Sixth National Conference on Artificial Intelligence, pages 268-272, Menlo Park. CA. American Association for Arti­ ficial Intelligence.'
    try:
        os.chdir(GROBID_HOME)
        subprocess.call(GROBID_COMMAND + '"%s"' % citation_string, shell=True)
    except:
        pass


if __name__ == "__main__":
    """  """
    main()
