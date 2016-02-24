package com.skymiracle.tree;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skymiracle.json.JSONTools;

public class TreeNode {

	private Object infoObject;

	private Collection<TreeNode> subNodes;

	public TreeNode(Object infoObject, Collection<TreeNode> subNodes) {
		super();
		this.infoObject = infoObject;
		this.subNodes = subNodes;
	}

	public Object getInfoObject() {
		return this.infoObject;
	}

	public void setInfoObject(Object infoObject) {
		this.infoObject = infoObject;
	}

	public Collection<TreeNode> getSubNodes() {
		return this.subNodes;
	}

	public void setSubNodes(Collection<TreeNode> subNodes) {
		this.subNodes = subNodes;
	}

	public static void scan(TreeNode treeNode, TreeNodeDealer tnd)
			throws Exception {
		tnd.dealWithSubTreeNode(treeNode);
		for (TreeNode subTreeNode : treeNode.subNodes) {
			tnd.dealWithSubTreeNode(subTreeNode);
			scan(subTreeNode, tnd);
		}
	}

	public JSONObject getJSONObject() throws Exception {
		return getJSONObject(this);
	}

	public static JSONObject getJSONObject(TreeNode treeNode) throws Exception {
		JSONObject jo = new JSONObject();
		jo.put("info", JSONTools.getJSONObject(treeNode.getInfoObject()));
		JSONArray ja = new JSONArray();
		for (TreeNode subTreeNode : treeNode.subNodes)
			ja.put(getJSONObject(subTreeNode));
		jo.put("children", ja);
		return jo;
	}
}
