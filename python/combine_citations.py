#!/usr/bin/env python3
"""
Combine citation strings into a single file
"""
import glob

__author__ = "William A. Ingram"
__version__ = "0.1.0"
__license__ = "BSD3"


def main():
    """  """

    with open("/Users/waingram/Desktop/sample17-ran100-v2/sample17-ran100-v2.citestr_combined.txt", "w") as outfile:
        for n in range(00, 101):
            if n == 2:
                continue
            f = '/Users/waingram/Desktop/sample17-ran100-v2/sample17-ran100-v2.citestr_%02d.txt' % n
            with open(f, "r") as infile:
                outfile.write(infile.read() + '\n')


if __name__ == "__main__":
    """  """
    main()
