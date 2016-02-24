package com.skymiracle.mdo5;

import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.skymiracle.io.Dir;
import com.skymiracle.logger.Logger;
import com.skymiracle.mdo5.MdoReflector.MdoField;
import com.skymiracle.sor.exception.AppException;
import com.skymiracle.util.StringUtils;

public class MdoFullTextSearcher<T extends Mdo<T>> {

	private Class<T> mdoClass;

	private File indexDir;

	public MdoFullTextSearcher(Class<T> mdoClass, File indexDir) {
		super();
		this.mdoClass = mdoClass;
		this.indexDir = indexDir;
	}

	public void addToIndex(List<T> mdos) throws AppException, Exception {
		IndexWriter writer = new IndexWriter(FSDirectory.open(indexDir),
				new StandardAnalyzer(Version.LUCENE_30), false,
				IndexWriter.MaxFieldLength.LIMITED);
		MdoField[] mFields = MdoReflector.getMdoFields(mdoClass);
		for (T mdo : mdos) {
			Document doc = new Document();
			for (MdoField mField : mFields) {
				Field field = null;
				if (StringUtils.isIncluded(mdo.keyNames(), mField.name))
					field = new Field(mField.name, String.valueOf(mdo
							.fieldValue(mField.name)), Field.Store.YES,
							Field.Index.NO);
				else if (mField.type == StringBuffer.class)
					field = new Field(mField.name, String.valueOf(mdo
							.fieldValue(mField.name)), Field.Store.NO,
							Field.Index.ANALYZED);
				if (field != null)
					doc.add(field);
			}
			writer.addDocument(doc);
		}
		writer.optimize();
		writer.close();
	}

	public void addToIndex(T mdo) throws AppException, Exception {
		MList<T> mdos = new MList<T>();
		mdos.add(mdo);
		addToIndex(mdos);
	}

	public void clearIndex() throws AppException, Exception {
		new Dir(this.indexDir).delAllFile();
		create();

	}

	public void create() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(FSDirectory.open(indexDir),
					new StandardAnalyzer(Version.LUCENE_30), true,
					IndexWriter.MaxFieldLength.LIMITED);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null)
				try {
					writer.optimize();
					writer.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

		}
	}

	public MList<T> search(int pageNum, int countPerPage, String keyword,
			String field) throws AppException, Exception {
		if (pageNum <= 0)
			throw new RuntimeException("pageNum must > 0");
		MList<T> mdos = new MList<T>();
		IndexReader reader = IndexReader.open(FSDirectory.open(indexDir));
		Searcher searcher = new IndexSearcher(reader);
		QueryParser parser = new QueryParser(Version.LUCENE_30, field,
				new StandardAnalyzer(Version.LUCENE_30));

		Query query = parser.parse(keyword);
		int count = (pageNum + 5) * countPerPage;
		Logger.debug("FullTextSearch query=" + query);
		TopScoreDocCollector collector = TopScoreDocCollector.create(count,
				false);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			T mdo = mdoClass.newInstance();
			for (MdoField mField : mdo.getMdoFields()) {
				String v = searcher.doc(i).get(mField.name);
				if (v == null)
					continue;
				mdo.fieldValue(mField.name, v);
			}
			mdos.add(mdo);
		}
		searcher.close();
		reader.close();
		return mdos;
	}

}
