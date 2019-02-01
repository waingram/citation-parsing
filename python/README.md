# Third-party Tools
Use these commands to extract references from PDFs.

CERMINE:

    $ java -Xmx2G -cp cermine-impl-1.14-20180204.213009-17-jar-with-dependencies.jar pl.edu.icm.cermine.ContentExtractor -path ../vtetds/ -outputs bibtext
    
GROBID:

    $ java -Xmx2G -jar grobid-core-0.5.3-onejar.jar -gH grobid-home -dIn ../vtedts/ -dOut ../refs/grobid -exe processReferences
    
 
