import sys
import numpy as np

# r_tags={0,1,2,3,4,5,6,7,8,9,10,11,12}
# map_dict={'institution': 4, 'journal': 5, 'booktitle': 1, 'pages': 8,  'author': 0,
# 'note': 7, 'editor': 3, 'publisher': 9, 'location': 6, 'date': 2, 'tech':10, 'title':11, 'volume':12}
r_tags = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}
map_dict = {'author': 0,
            'booktitle': 1,
            'year': 2,
            'editor': 3,
            'institution': 4,
            'journal': 5,
            'location': 6,
            'note': 7,
            'pages': 8,
            'publisher': 9,
            'title': 10,
            'volume': 11,
            'X': 12}
confusion = np.ndarray((len(r_tags),len(r_tags)),dtype=float) 
#confusion.fill(0.00000000000001)
confusion.fill(1)
# print confusion
filename = '/Users/waingram/Projects/citation-parsing/grobid_analysis.txt'
k = open(filename, 'r')
for lines in k:
    a=lines.strip().split()
    #print a
    if len(a)==0:
        continue
    confusion[map_dict[a[-2]]][map_dict[a[-1]]] = confusion[map_dict[a[-2]]][map_dict[a[-1]]]+1
    tp = np.zeros(len(set(r_tags)))
    fp = np.zeros(len(set(r_tags)))
    fn = np.zeros(len(set(r_tags)))
    f1 = np.zeros(len(set(r_tags)))
    for j in xrange(len(set(r_tags))):
        tp[j] = confusion[j][j]
        fp[j] = np.sum(confusion, axis = 1)[j] - tp[j]
        fn[j] = np.sum(confusion, axis = 0)[j] - tp[j]
        f1[j] = float(2*tp[j])/float(2*tp[j] + fn[j] +fp[j])

print 'True Distribution'
print np.sum(confusion,axis=1)
print 'F1s'
print f1
print 'Print average'
print np.average(f1)


print confusion
