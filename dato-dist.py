import graphlab as gl
import datetime

# Create cluster
c = gl.deploy.hadoop_cluster.create(name='test-cluster',dato_dist_path='hdfs://ec2-54-215-136-187.us-west-1.compute.amazonaws.com:9000/dato/tmp',hadoop_conf_dir='/usr/local/hadoop/etc/hadoop',num_containers=3)
print c

from graphlab import SFrame, SGraph
url = 'hdfs://ec2-54-215-136-187.us-west-1.compute.amazonaws.com:9000/data/pokec.txt'
data = SFrame.read_csv(url, delimiter='\t',header=False)
g = SGraph().add_edges(data, src_field='X2', dst_field='X1')


# triangle counting
from graphlab import triangle_counting
tc = triangle_counting.create(g)
tc_out = tc['triangle_count']


#pagerank
from graphlab import pagerank
datetime.datetime.now()
pr = pagerank.create(g,threshold=0.001)
datetime.datetime.now()


# Connected Components
from graphlab import connected_components
datetime.datetime.now()
cc = connected_components.create(g)
datetime.datetime.now()
