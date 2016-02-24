package com.skymiracle.mdo5;

import java.util.LinkedList;
import java.util.List;

import com.skymiracle.html.Table;
import com.skymiracle.html.Td;
import com.skymiracle.html.Tr;

public class PagedList<E extends Mdo<E>> extends MList<E> {

	private static final long serialVersionUID = -2560861289716236684L;

	private int pageNum;

	private int countPerPage;

	private int allCount = 0;

	private String linkPrefix;

	public String pageNumArgName = "pageNum";

	public String countPerPageArgName = "countPerPage";

	public PagedList() {
		super();
	}

	public PagedList(int pageNum, int countPerPage, int allCount,
			String linkPrefix) {
		this.pageNum = pageNum;
		this.countPerPage = countPerPage;
		this.allCount = allCount;
		this.linkPrefix = linkPrefix;
	}

	public int getAllCount() {
		return allCount;
	}

	public int getCountPerPage() {
		return countPerPage;
	}

	public int getPageNum() {
		return pageNum;
	}

	protected void setAllCount(int allCount) {
		this.allCount = allCount;
	}

	protected void setCountPerPage(int countPerPage) {
		this.countPerPage = countPerPage;
	}

	protected void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public void setLinkPrefix(String linkPrefix) {
		this.linkPrefix = linkPrefix;
	}

	public String getLinkPrefix() {
		return linkPrefix;
	}

	public String getPageNumArgName() {
		return pageNumArgName;
	}

	public void setPageNumArgName(String pageNumArgName) {
		this.pageNumArgName = pageNumArgName;
	}

	public String getCountPerPageArgName() {
		return countPerPageArgName;
	}

	public void setCountPerPageArgName(String countPerPageArgName) {
		this.countPerPageArgName = countPerPageArgName;
	}

	public int getPageCount() {
		if (countPerPage == 0)
			countPerPage = 1;
		long t = allCount % countPerPage == 0 ? allCount / countPerPage
				: allCount / countPerPage + 1;
		if (t == 0)
			t = 1;
		return (int) t;
	}

	public String getLink(int pageNum) {
		StringBuffer buf = new StringBuffer();
		if (linkPrefix.indexOf("?") < 0)
			buf.append(linkPrefix + "?");
		else
			buf.append(linkPrefix + "&");

		return buf.append(pageNumArgName).append("=").append("" + pageNum)
				.append("&").append("" + countPerPageArgName).append(
						"=" + countPerPage).toString();
	}

	private List<Td> getPageBarHtmlTdTags() {
		int pageCount = getPageCount();
		List<Td> tds = new LinkedList<Td>();
		tds.add(new Td().setContent("每页" + countPerPage + "条"));
		tds.add(new Td().setContent("共" + allCount + "条"));
		tds.add(new Td().setContent("当前 " + pageNum + "/" + pageCount + " 页"));
		tds.add(new Td().setId("pageBarFirst").setContent(
				"<a href='" + getLink(1) + "'>首页</a>"));

		if (pageNum == 1)
			tds.add(new Td().setId("pageBarPrev")
					.setContent("<span>上一页</span>"));
		else
			tds.add(new Td().setId("pageBarPrev").setContent(
					"<a href='" + getLink(this.pageNum - 1) + "'>上一页</a>"));

		if (pageNum == pageCount)
			tds.add(new Td().setId("pageBarNext")
					.setContent("<span>下一页</span>"));
		else
			tds.add(new Td().setId("pageBarNext").setContent(
					"<a href='" + getLink(this.pageNum + 1) + "'>下一页</a>"));

		tds.add(new Td().setId("pageBarLast").setContent(
				"<a href='" + getLink(pageCount) + "'>末页</a>"));

		return tds;
	}

	public String getPageBarTdsHTML() {
		StringBuffer sb = new StringBuffer();
		for (Td td : getPageBarHtmlTdTags())
			sb.append(td.toString());
		return sb.toString();
	}

	public String getPageBarHTML() {
		Table table = new Table();
		Tr tr = table.addTr();
		for (Td td : getPageBarHtmlTdTags()) {
			tr.add(td);
		}
		return table.toString();

	}
	
	public String getTextBarHTML() {
		int pageCount = getPageCount();
		
		StringBuffer buf = new StringBuffer();
		buf.append("每页" + countPerPage + "条&nbsp;");
		buf.append("共" + allCount + "条&nbsp;");
		buf.append("当前 " + pageNum + "/" + pageCount + " 页&nbsp;&nbsp;");
		
		buf.append("<a href='" + getLink(1) + "'>首页</a>&nbsp;");
		
		if (pageNum == 1)
			buf.append("<span>上一页</span>&nbsp;");
		else
			buf.append("<a href='" + getLink(this.pageNum - 1) + "'>上一页</a>&nbsp;");

		if (pageNum == pageCount)
			buf.append("<span>下一页</span>&nbsp;");
		else
			buf.append("<a href='" + getLink(this.pageNum + 1) + "'>下一页</a>&nbsp;");

		buf.append("<a href='" + getLink(pageCount) + "'>末页</a>");
		
		return buf.toString();
	}
}
