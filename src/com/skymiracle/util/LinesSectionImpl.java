package com.skymiracle.util;

import java.util.ArrayList;

public abstract class LinesSectionImpl implements LinesSection {
	protected int begin;

	protected int end;

	protected ArrayList linesList;

	public LinesSectionImpl(ArrayList linesList, int begin, int end) {
		this.linesList = linesList;
		this.begin = begin;
		this.end = end;
	}

	public LinesSectionImpl(ArrayList linesList) {
		this(linesList, 0, linesList.size());
	}

	public LinesSection getFirstSection() {
		int subEnd = this.end;
		for (int i = this.begin; i < this.end; i++) {
			if (lineEqualsStr(i, "")) {
				subEnd = i;
				break;
			}
		}
		return getInstance(this.linesList, this.begin, subEnd);
	}

	protected abstract LinesSection getInstance(ArrayList linesList, int begin,
			int end);

	protected abstract boolean lineEqualsStr(int lineIndex, String str);

	public int getBegin() {
		return this.begin;
	}

	public int getEnd() {
		return this.end;
	}

	public LinesSection getNoFirstSection() {
		LinesSection lsfs = this.getFirstSection();
		int subBegin = lsfs.getEnd();
		for (int i = lsfs.getEnd() + 1; i < this.end; i++) {
			if (!lineEqualsStr(i, "")) {
				subBegin = i;
				break;
			}
		}
		return getInstance(this.linesList, subBegin, this.end);
	}

	public LinesSection[] getSections(String boundary) {
		LinesSection[] result = null;
		ArrayList lsList = new ArrayList();
		int subBegin = -1;
		int subEnd = this.end;
		String beginBoundary = "--" + boundary;
		String endBoundary = "--" + boundary + "--";
		for (int i = this.begin; i < this.end; i++) {
			if (lineEqualsStr(i, beginBoundary)) {
				if (subBegin == -1) {
					subBegin = i + 1;
				} else {
					subEnd = i;
					LinesSection ls = getInstance(this.linesList, subBegin,
							subEnd);
					lsList.add(ls);
					subBegin = i + 1;
				}
			} else if (lineEqualsStr(i, endBoundary)) {
				if (subBegin == -1)
					subBegin = 0;
				subEnd = i;
				LinesSection ls = getInstance(this.linesList, subBegin, subEnd);
				lsList.add(ls);
				subBegin = i + 1;
			}
		}

		result = new LinesSection[lsList.size()];
		return (LinesSection[]) lsList.toArray(result);
	}

}
