# Dato vs. GraphX
Some naive comparisons of two graph processing tools: Dato vs Spark GraphX. 
Thanks to <a href= "http://insightdataengineering.com/" target="_blank">Insight Data Engineering Fellow Program</a> for providing the cluster to run these experiments.
Slides available <a href= "http://www.slideshare.net/KeiraZhou2/dato-vs-graphx" target="_blank">here</a>.


##Table of Contents
- <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/README.md#aws-cluster">AWS Cluster</a>
- <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/README.md#dato">Dato</a>
- <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/README.md#graphx">GraphX</a>
- <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/README.md#experiments">Experiments</a>
- <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/README.md#results">Results</a>
- <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/README.md#conclusion">Conclusion</a>


##AWS Cluster

All the experiments are run on a cluster with 1 master node and 3 work nodes on <a href= "https://aws.amazon.com/" target="_blank">AWS</a>. Each nodes is a m4.large instances with 8GB of RAM with 2 cores. 


##Dato
<a href= "https://dato.com/" target="_blank">Dato</a> is a graph-based, asynchronous, high performance, distributed computation framework written in C++. Dato provides 30-days free trial of accessing their products. You can download the free trial by registering <a href= "https://dato.com/download/" target="_blank">here</a>.  Following covers the basics of setting up Dato Distributed on AWS.

- **GraphLab Create:** You need to install GraphLab Create on a local machine to be able to interact with a distributed Dato cluster. To install, simply follow <a href= "https://dato.com/download/install-graphlab-create.html" target="_blank">Dato installation guide</a>. Here, I will also provide the basic steps to set things up in Linux environment.
  - Downlaod: Assume that you have registered with your email and have received a product key.
    - run ```sudo pip install --upgrade --no-cache-dir https://get.dato.com/GraphLab-Create/1.6.1/EMAIL/PRODUCT_KEY/GraphLab-Create-License.tar.gz```
    - Replace EMAIL and PRODUCT_KEY with your personal information and let Dato take care of the rest
  - Test the Installation: For more details, please visit <a href= "https://dato.com/learn/userguide/install.html" target="_blank">GraphLab Create Getting Started</a>.

    ```{python}
    import graphlab as gl
    url = 'http://s3.amazonaws.com/dato-datasets/millionsong/song_data.csv'
    songs = gl.SFrame.read_csv(url)
    songs['year'].mean()
    
    # Regression Model:
    url = 'http://s3.amazonaws.com/dato-datasets/regression/Housing.csv'
    x = gl.SFrame.read_csv(url)
    m = gl.linear_regression.create(x, target='price')
    ```

- **Dato Distributed:** After you have GraphLab Create installed, now you can move on to install Dato Distributed on a cluster. You will need to download Dato Distributed on your cluster as well as your personalized license file: Dato-Distributed-Services.ini. Dato utilizes Hadoop YARN cluster configuration. Here I will go through the basic steps for setting up Dato Distributed on a Hadoop cluster. For more details, please visit <a href= "https://dato.com/learn/userguide/deployment/pipeline-hadoop-setup.html" target="_blank">Setting up Dato Distributed on Hadoop</a>.
  - Deploy Dato Distributed: Go to Dato Distributed directory

    ```{engine='sh'}
    ./setup_dato-distributed.sh -d <HDFS_DIR_FOR_INSTALL_DATO> (eg. hdfs://your_cluster_ip:9000/dato/tmp)
                                -k dato_license.ini 
                                -c <HADOOP_BIN_PATH> (eg. /usr/local/hadoop/etc/hadoop)
                                -p <NODE_TMP_DIR> (eg. /mnt/my-data/dato/tmp)
    ```
  - Test the cluster
    ```{python}
    import graphlab as gl

    # Create cluster
    c = gl.deploy.hadoop_cluster.create(name='test-cluster',
                                dato_dist_path='hdfs://your_cluster_ip:9000/dato/tmp',
                                hadoop_conf_dir='/usr/local/hadoop/etc/hadoop',
                                num_containers=3)
    print c
    ```

##GraphX
<a href= "https://spark.apache.org/docs/1.1.0/graphx-programming-guide.html" target="_blank">GraphX</a> is the new (alpha) Spark (written in Scala) API for graphs and graph-parallel computation. At a high-level, GraphX extends the Spark RDD by introducing the Resilient Distributed Property Graph: a directed multigraph with properties attached to each vertex and edge. You will be able to run GraphX after installing Spark.
- To use GraphX, simply import the packages in Spark: 
 
  ```{scala}
  import org.apache.spark._
  import org.apache.spark.graphx._
  ```

##Experiments

**Graph Algorithms:** Both Dato Distributed and GraphX have graph algorithms Triangle-counting, PageRank and Connected Components. The experiments will be testing all three algorithms on the following datasets. The sample code can be found: <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/dato-dist.py" target="_blank">dato-dist.py</a> for Dato Distributed and <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/graphx.scala" target="_blank">graphx.scala</a> for GraphX. The expriments all start with default settings. GraphX is set to 1G per executor memory. Dato has total 4G for GRAPHLAB\_FILEIO\_MAXIMUM\_CACHE\_CAPACITY. GraphX is later set to 2G per executor memory.

**Dataset:**
The graph datasets are downloaded from <a href= "https://snap.stanford.edu/data/" target="_blank">Stanford Large Network Dataset Collection (SNAP)</a>. The datasets are then uploaded into HDFS. In these experiments, following 4 datasets were used:
- Facebook: <a href= "https://snap.stanford.edu/data/egonets-Facebook.html" target="_blank">facebook_combined.txt.gz</a>
  + Nodes: 4039 | Edges: 88234 | Number of triangles: 1612010
- YouTube: <a href= "https://snap.stanford.edu/data/com-Youtube.html" target="_blank">com-youtube.ungraph.txt.gz</a>
  + Nodes: 1134890 | Edges: 2987624 | Number of triangles: 3056386
- Pokec: <a href= "https://snap.stanford.edu/data/soc-pokec.html" target="_blank">soc-pokec-relationships.txt.gz</a>
  + Nodes: 1632803 | Edges: 30622564 | Number of triangles: 32557458
- LiveJournal: <a href= "https://snap.stanford.edu/data/com-LiveJournal.html" target="_blank">com-lj.ungraph.txt.gz</a>
  + Nodes: 3997962 | Edges: 34681189 | Number of triangles: 177820130

All the datasets are in the format of: [source][(delimiter)][destiny]. An example of downloading the datasets from Linux command line:
  - Download: ```wget https://snap.stanford.edu/data/facebook_combined.txt.gz```
  - Unzip: ```gunzip facebook_combined.txt.gz```
  - Upload to HDFS: ```hdfs dfs -copyFromLocal facebook_combined.txt.gz /data```


##Results
- Triangle Counting
  - For Triangle Counting, both Dato and GraphX (if it finishes the job) returns the correct answer as listed on the SNAP website.
  
  <img src="https://github.com/keiraqz/dato-vs-graphx/blob/master/img/tri.png" alt="alt text" width="500" height="300">

  - Dato: for all datasets, Dato can load and process the graph with default settings. 
  - GraphX: 
    - For Facebook data: Can load and process with default settings. 
    - For YouTube data: Can load the data, but have trouble processing it with 1G executor memory. Increase executor memory to 2G. Worked.
      - Some errors run into: 
      
      ```ERROR TaskSchedulerImpl: Lost executor 1 on 172.31.11.231: remote Rpc client disassociated```
    - For Pokec and LiveJournal data: Have trouble loading data with 1G executor memory. Increase executor memory to 2G. Data can be loaded but can’t finish the computation for triangle count.
      - Some errors run into:
        
      ``` 
      java.util.concurrent.TimeoutException: Futures timed out after [120 seconds]
      Lost executor 2 on *: remote Rpc client disassociated
      ERROR TaskSchedulerImpl: Lost executor 1 on *: remote Rpc client disassociated
      ERROR TaskSchedulerImpl: Lost executor 4 on *: OutOfMemoryError
      ```
    - What I observed was that certain stages within the job kept failing. A stage in Spark will operate on one partition of the RDD at a time (and load the data in that partition into memory) <a href="http://blog.cloudera.com/blog/2015/05/working-with-apache-spark-or-how-i-learned-to-stop-worrying-and-love-the-shuffle/" target="_blank">[1]</a>. Besides increasing the executor memory, another possible solution is to increase the number of partitions of the RDD so that each stage is processing smaller amount of data.

- PageRank
  - The threshold for PageRank is set to 0.001.
  
    <img src="https://github.com/keiraqz/dato-vs-graphx/blob/master/img/page.png" alt="alt text" width="500" height="300">

  - It's interesting that Pokec data took longer to process for PageRank algorithm. This may caused by connectivity of the graph.


- Connected Components
    
  <img src="https://github.com/keiraqz/dato-vs-graphx/blob/master/img/cc.png" alt="alt text" width="500" height="300">


##Conclusion
Although these experiments are quick setups for both of the tools without fine-tune runtime parameters, from the results we still can have some ideas that Dato has clear advantages over GraphX in terms of execution time for processing large scale graph data. Dato is also really easy to deploy. However, GraphX is free while Dato charges a service fee after the free trial.

In addition, it is often necessary to be able to move between table and graph views of the same physical data and to leverage the properties of each view to easily and efficiently express computation. The goal of the GraphX project is to unify graph-parallel and data-parallel computation in one system with a single composable API. 

Further experiments can be done to compare the overall performance of a specific task that contains both graph algorithms and other data-parallel computation.

- Further readings: <a href="http://arxiv.org/pdf/1402.2394.pdf" target="_blank">Here</a> is a really good research paper by UC Berkeley of GraphX.
