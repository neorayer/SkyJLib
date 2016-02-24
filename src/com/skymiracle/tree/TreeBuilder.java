package com.skymiracle.tree;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.skymiracle.reflect.ReflectTools;

public class TreeBuilder {

	private List objs;

	private String keyName;

	private String fatherKeyName;

	private Map<Object, Object> keyIndexMap = new HashMap<Object, Object>();

	public TreeBuilder(List grps, String keyName, String fatherKeyName)
			throws Exception {
		this.objs = grps;
		this.keyName = keyName;
		this.fatherKeyName = fatherKeyName;
		// keyIndexMap
		for (Object obj : grps) {
			Object keyValue = getKeyValue(obj);
			this.keyIndexMap.put(keyValue, obj);
		}
	}

	private Object getKeyValue(Object object) throws Exception {
		Method method = ReflectTools.getGetterMethod(object.getClass(),
				this.keyName);
		return method.invoke(object, new Object[0]);
	}

	public TreeNode getTree() throws Exception {
		if (this.objs == null)
			throw new Exception("objs is empty!");

		Object root = getRootObject();
		return getTreeNode(root);
	}

	private TreeNode getTreeNode(Object object) throws Exception {
		Object keyValue = getKeyValue(object);
		List<TreeNode> list = new LinkedList<TreeNode>();
		for (Object obj : this.objs) {
			Object fatherKeyValue = getFatherKeyValue(obj);
			if (keyValue.equals(fatherKeyValue)) {
				TreeNode subNode = getTreeNode(obj);
				list.add(subNode);
			}
		}
		return new TreeNode(object, list);
	}

	private Object getRootObject() throws Exception {
		Object first = this.objs.get(0);
		return getRootObject(first);
	}

	private Object getRootObject(Object object) throws Exception {
		Object father = getFatherObject(object);
		if (null == father)
			return object;
		else
			return getRootObject(father);
	}

	private Object getFatherObject(Object object) throws Exception {
		Object fatherKeyValue = getFatherKeyValue(object);
		return this.keyIndexMap.get(fatherKeyValue);
	}

	private Object getFatherKeyValue(Object object) throws Exception {
		Method method = ReflectTools.getGetterMethod(object.getClass(),
				this.fatherKeyName);
		return method.invoke(object, new Object[0]);
	}

}
