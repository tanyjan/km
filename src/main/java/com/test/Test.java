package com.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.Function;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;

public class Test {
	public static MongoClient client = null;

	public static MongoClient create() {
		if(null!=client) {
			return client;
		}
		try {
			String host = "127.0.0.1";
			int port = 27017;
			client = new MongoClient(host, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return client;
	}

	public static void main(String[] args) {
		create();
		MongoCollection<Document> collection = client.getDatabase("testdb").getCollection("test1");
//		BasicDBObject bson = new BasicDBObject("name", "tx");
//		Document doc = new Document();
//		doc.append("name", "admin").append("age", 11);
//		collection.insertOne(doc);
//		FindIterable<Document> res2 = collection.find(bson);

		long counts = collection.count();
		System.out.println(counts);
		FindIterable<Document> res = collection.find().sort(new BasicDBObject("age", 1));
		MongoCursor<Document> cursor = res.iterator();
		for(Iterator<Document> it = cursor;cursor.hasNext();) {
			System.out.println(it.next().toJson());
		}

		System.out.println(counts);
		res = collection.find().sort(new BasicDBObject("age", -1));
		MongoCursor<Map<String, Object>> cursor1 = res.map(new Function<Document, Map<String, Object>>() {

			@Override
			public Map<String, Object> apply(Document t) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", t.get("name"));
				map.put("age", t.get("age"));
				System.out.println(t.toJson());
//				DeleteResult dr = collection.deleteOne(new BasicDBObject("name", t.get("name")));
//				System.out.println("delete count: "+ dr.getDeletedCount());
				return map;
			}
		}).iterator();
		for(Iterator<Map<String, Object>> it = cursor1;cursor1.hasNext();) {
			System.out.println(it.next());
		}
		
		Document docu = collection.findOneAndUpdate(new BasicDBObject("name", "admin"), new BasicDBObject("sq_id", 1));
//		collection.findOneAndReplace(new BasicDBObject("name", "admin"), new Document("name", "admin").append("sq_id", 1));
//		while(true) {
//			Document docu = collection.findOneAndUpdate(new BasicDBObject("name", "admin"), new BasicDBObject("sq_id", 1));
//			System.out.println(docu);
//		}
	}

}
