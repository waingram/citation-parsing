from sklearn.metrics import f1_score, precision_score, recall_score
input_experiment='cora.txt_outf'

for i in xrange(10):
	filename = input_experiment+str(i)+'.output'
	f= open(filename,'r')
	y_true=[]
	y_pred=[]
	for line in f:
		if line.strip()=='':
			pass
		else:
			segments=line.strip().split('\t')
			y_true.append(segments[-2])
			y_pred.append(segments[-1])
print recall_score(y_true, y_pred, average=None)
print precision_score(y_true, y_pred, average=None)
print f1_score(y_true, y_pred, average=None)
