import numpy as np
import time
from scipy.cluster.hierarchy import fclusterdata
def printTime():
    # 格式化成2016-03-20 11:45:39形式
    print(time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()))
def mydist(s1, s2):
    w1 = s1.split(' ')
    w2 = s2.split(' ')
    n1 = len(w1)
    n2 = len(w2)
    #res = n1*(n1+1)/2 + n2*(n2+1)/2 #for most of time, =len(v1|v2)+len(v1&v2) except for same ie. a a a 
    v1 = set()
    v2 = set()
    for i in range(0, n1):
        tw = ''
        for j in range(i, n1):
            if (j==i):
                tw = w1[j]
            else:
                tw = tw + '' + w1[j]
            v1.add(tw)
    for i in range(0, n2):
        tw = ''
        for j in range(i, n2):
            if (j==i):
                tw = w2[j]
            else:
                tw = tw + '' + w2[j]
            v2.add(tw)
    vec1 = v1&v2
    vec2 = v1|v2
    tot1 = 0
    tot2 = 0
    for i in vec1:
        #print(i)
        tot1 = tot1 + len(i)
    for i in vec2:
        tot2 = tot2 + len(i)
    #print(tot1)
    #print(tot2)
    ans = tot1/tot2
    #print(type(ans))
    #print(s1+'------'+s2+' '+str(ans))
    return 1-ans
    #print(w1)
    #print(w2)

def mydist2(n1, n2):
    #print(n1, '***', n2)
    #print(n1-n2)
    return 1.0*abs(n1-n2)

def calcDistance(n1, n2):
    #print(n1)
    i1 = int(n1[0])
    #print(i1)
    i2 = int(n2[0])
    return mydist(data[i1], data[i2])

printTime()
a = np.array([1, 2], dtype=np.str)
print(a)
with open("AgglomerativeCluster/usage_data/test.txt", encoding='utf-8') as f:
	data = f.readlines()
for i in range(0, len(data)):
    sentence = data[i]
    sentence = sentence[0:len(sentence)-1] #remove the \n in the back of each sentence
    data[i] = sentence
mydist('a b c', 'c a b')
data = data[:10] #choose first 100 to test
#for i in data:
#    print(i)
data = np.array(data)
#print(data)
X = np.asarray(data, order='c', dtype=np.str)
#exit(0)
indices = [[i] for i in range(0,len(data))]
indices = np.array(indices)
#print(indices)
fclust = fclusterdata(indices,0.7,method='complete',metric=calcDistance)#, metric = mydist2)#, metric=mydist)
print(fclust)
h = set(fclust)
print(len(h))
#print('LEN::::'+str(len(fclust)))
for i in range(0,len(data)):
    print(str(i)+':   ',end='')
    cnt = 0
    for j in range(0,len(fclust)):
        cnt = cnt+1
        if (fclust[j] == i):
            print(str(data[j]),end='; ')
    print('')
printTime()
