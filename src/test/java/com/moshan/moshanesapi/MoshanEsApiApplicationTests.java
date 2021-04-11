package com.moshan.moshanesapi;

import com.alibaba.fastjson.JSON;
import com.moshan.moshanesapi.entity.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.text.Highlighter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @Description  魔山讲解 es7.9.0 高级客户都测试API
 * @author  zyc
 * @Datetime  2021/4/10 17:07
 */
@SpringBootTest
class MoshanEsApiApplicationTests {

	@Autowired
	@Qualifier(value = "restHighLevelClient")
	RestHighLevelClient client;

	// 测试创建索引 Request PUT moshan_index
	@Test
	void testCreateIndex() throws IOException {
		//1.创建索引请求
		CreateIndexRequest request = new CreateIndexRequest("moshan_index");
		//2.客户端执行请求	 IndicesClient,请求后获得响应
		CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

		System.out.println(createIndexResponse);
	}

	//测试获取索引,判断其是否存在
	@Test
	void testExistIndex() throws IOException {
		GetIndexRequest request = new GetIndexRequest("moshan_index");
		boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
		System.out.println(exists);
	}

	//测试删除索引
	@Test
	void testDeleteIndex() throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest("moshan_index");
		AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
		System.out.println(delete);
	}
	//测试添加文档
	@Test
	void testAddDocument() throws IOException {
		//创建对象
		User user =new User("魔山",3);
		//创建请求
		IndexRequest request = new IndexRequest("moshan_index");

		// 规则 put /kuang_index/_doc/1
		request.id("1");
		request.timeout(TimeValue.timeValueSeconds(1));
		request.timeout("1s");

		//将我们的数据放入请求 json
		request.source(JSON.toJSONString(user), XContentType.JSON);

		// 客户端发送请求，获取响应结果
		IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

		System.out.println(indexResponse.toString());
		System.out.println(indexResponse.status());//对应我们命令返回的状态 CREATED
	}

	//获取文档 判断文档是否存在 get /index/doc/1
	@Test
	void testIsExists() throws IOException {
		GetRequest getRequest = new GetRequest("moshan_index", "1");

		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");
		boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
		System.out.println(exists);
	}

	//获得文档的信息
	@Test
	void testGetDocument() throws IOException {
		GetRequest getRequest = new GetRequest("moshan_index", "1");
		GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
		System.out.println(getResponse.getSourceAsString());
		System.out.println(getResponse);
	}
	//更新文档的信息
	@Test
	void testUpdateRequest() throws IOException {
		UpdateRequest updateRequest = new UpdateRequest("moshan_index", "6");
		updateRequest.timeout("1s");

		User user = new User("java", 22);
		updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
		UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
		System.out.println(updateResponse.status());
	}

	//删除文档记录
	@Test
	void testDeleteRequest() throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest("moshan_index", "1");
		deleteRequest.timeout("1s");

		DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
		System.out.println(deleteResponse.status());
	}

	@Test
	void testBulkRequest() throws IOException {
		BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.timeout("10s");

		ArrayList<Object> userList = new ArrayList<>();
		userList.add(new User("java",22));
		userList.add(new User("C++",23));
		userList.add(new User("C#",24));
		userList.add(new User("php",26));
		userList.add(new User("go",25));
		userList.add(new User(".net",27));
		for (int i = 0; i < userList.size(); i++) {
			//批量更新和批量删除，就在这里修改对应的请求就可以了
			bulkRequest.add(new IndexRequest("moshan_index")
							.id(""+(i+1))//不写id的话 就随机生成
							.source(JSON.toJSONString(userList.get(i)),XContentType.JSON));
		}
		BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
		System.out.println(bulkResponse.hasFailures());//是否失败  false 成功
	}


	// 查询
	// SearchRequest 搜索请求
	// SearchSourceBuilder 条件构造 里面好多方法
	// HighlightBuilder 构建高亮
	// TermQueryBuilder 构建精确查询
	// MatchAllQueryBuilder 构建查询全部的查询
	// xxxQueryBuilder 对应我们刚才看到的命令！
	@Test
	void testSearch() throws IOException {
		SearchRequest searchRequest = new SearchRequest("moshan_index");
		SearchSourceBuilder searchSourceBuilder =new SearchSourceBuilder();

		//searchSourceBuilder.highlighter(); 可以设置高亮
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "java");

		searchSourceBuilder.query(termQueryBuilder);
		// juc 并发安全的时间使用模式
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		System.out.println(JSON.toJSONString(searchResponse.getHits()));
		System.out.println("========================");
		for (SearchHit documentFields : searchResponse.getHits().getHits()) {
			System.out.println(documentFields.getSourceAsMap());
		}
	}

}
