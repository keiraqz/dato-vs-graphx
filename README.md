# Dato vs. GraphX
Some naive comparisons of two graph processing tools: Dato vs Spark GraphX. 
Thanks to <a href= "http://insightdataengineering.com/" target="_blank">Insight Data Engineering Fellow Program</a> for providing the cluster to run these experiments.


##Table of Contents
- <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/README.md#settings">Settings</a>
- <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/README.md#dato">Dato</a>
- <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/README.md#graphx">GraphX</a>
- <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/README.md#results">Results</a>
- <a href= "https://github.com/keiraqz/dato-vs-graphx/blob/master/README.md#conclusion">Conclusion</a>


##Settings

**AWS Clusters:**
All the experiments are run on a cluster with 1 master node and 3 work nodes on <a href= "https://aws.amazon.com/" target="_blank">AWS</a>. Each nodes is a m4.large instances with 8GB of RAM with 2 cores. 

**Dataset:**
The graph datasets are downloaded from <a href= "https://snap.stanford.edu/data/" target="_blank">Stanford Large Network Dataset Collection</a>. In these experiments, following 4 datasets were used:
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
  - Unzip: ```gunzip com-youtube.ungraph.txt.gz```


##Dato
<a href= "https://dato.com/" target="_blank">Dato</a> is a graph-based, asynchronous, high performance, distributed computation framework written in C++. Dato provides 30-days free trial of accessing their products. You can download the free trial by registering <a href= "https://dato.com/download/" target="_blank">here</a>.  Following covers the basic of setting up Dato Distributed on AWS.

- **GraphLab Create:** You need to install GraphLab Create on a local machine to be able to interact with a distributed Dato cluster. To install, simply follow <a href= "https://dato.com/download/install-graphlab-create.html" target="_blank">Dato installation guide</a>. Here, I will also provide the basic steps to set things up in Linux environment.
  - Downlaod: Assume that you have registered with your email and have received a product key.
    - run ```sudo pip install --upgrade --no-cache-dir https://get.dato.com/GraphLab-Create/1.6.1/EMAIL/PRODUCT_KEY/GraphLab-Create-License.tar.gz```
    - Replace EMAIL and PRODUCT_KEY with your personal information and let Dato take care of the rest
  - Test the Installation: for more details, please visit <a href= "https://dato.com/learn/userguide/install.html" target="_blank">GraphLab Create Getting Started</a>

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


  - Batch producer: <a href= "https://github.com/keiraqz/artmosphere/blob/master/kafka/hdfs_producer.py" target="_blank">hdfs\_producer.py</a>
  - Batch consumer: <a href= "https://github.com/keiraqz/artmosphere/blob/master/kafka/hdfs_consumer.py" target="_blank">hdfs\_consumer.py</a>

- **Batch Processing (HDFS, Spark):** To perform batch processing job, Spark loads the data from HDFS and processed them in a distributed way. The two major batch processing steps for the project is to aggregate the artists upload locations and compute artwork-artwrok similarties. 
  - Aggreate Locations: <a href= "https://github.com/keiraqz/artmosphere/tree/master/batch_geo" target="_blank">batch\_geo</a>
    - To excute: run ```bash batch_geo_run.sh```
  - Compute Similarity: <a href= "https://github.com/keiraqz/artmosphere/blob/master/batch_similarity/compute_similarity.py" target="_blank">compute\_similarity.py</a>
    - To excute: run ```bash batch_sim_run.sh```
  
  The following graph shows the performance analysis of Spark for one the batch processing jobs - aggregating artists upload locations - up to 500GB:

  <img src="https://github.com/keiraqz/artmosphere/blob/master/img/Spark.png" alt="alt text" width="600">

- **Serving Layer (Elasticsearch, Cassandra):** The platform provides a search function that searches a given keyword within the artworks' title. In order to achieve this goal, the metadata of all artworks are stored into Elasticsearch. All artworks and artists are stored in Cassandra tables and can be retrieved by ids. The aggregated artists locations are also stored in Cassandra table, which can be queried by location\_code and timestamp.

- **Stream Processing (Spark Streaming):** Spark Streaming processes the data in micro batches. The job aggregates how many people collected a certain piece of art every 5 seconds and saves the result into a table in Cassandra. The information can be queried by artwork\_id and timestamp.
  - Streaming Processing: <a href= "https://github.com/keiraqz/artmosphere/tree/master/spark_streaming" target="_blank">spark\_streaming</a>
    - To excute: run ```bash log_streaming_run.sh```

- **Front-end (Flask, Bootstrap, Highcharts):** The frond-end uses Flask as the framework and the website uses JavaScript and Twitter Bootstrap libriries. All the plots are achieved via Highcharts. To visit: <a href="http://www.artmosphere.nyc"  target="_blank">www.artmosphere.nyc</a>


##GraphX


##Results


##Conclusion
