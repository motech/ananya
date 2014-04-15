package org.motechproject.ananya.domain;

import com.google.gson.Gson;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.ObjectUtils.compare;

public class BookMark implements Comparable<BookMark> {
	private static Logger log = LoggerFactory.getLogger(BookMark.class);
	@JsonProperty
	private String type;
	@JsonProperty
	private Integer chapterIndex;
	@JsonProperty
	private Integer lessonIndex;

	public BookMark() {
	}

	public BookMark(String type, Integer chapterIndex, Integer lessonIndex) {
		this.type = type;
		this.chapterIndex = chapterIndex;
		this.lessonIndex = lessonIndex;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BookMark bookMark = (BookMark) o;

		if (chapterIndex != null ? !chapterIndex.equals(bookMark.chapterIndex) : bookMark.chapterIndex != null)
			return false;
		if (lessonIndex != null ? !lessonIndex.equals(bookMark.lessonIndex) : bookMark.lessonIndex != null)
			return false;
		if (type != null ? !type.equals(bookMark.type) : bookMark.type != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = type != null ? type.hashCode() : 0;
		result = 31 * result + (chapterIndex != null ? chapterIndex.hashCode() : 0);
		result = 31 * result + (lessonIndex != null ? lessonIndex.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "BookMark{type='" + type + '\'' + ", chapterIndex='" + chapterIndex + '\'' + ", lessonIndex='" + lessonIndex + '\'' + '}';
	}

	public String getType() {
		return type;
	}

	public Integer getChapterIndex() {
		return chapterIndex;
	}

	public Integer getLessonIndex() {
		return lessonIndex;
	}

	public String asJson() {
		return new Gson().toJson(this);
	}

	/*  public boolean notAtPlayCourseResult() {
        return !Interaction.PlayCourseResult.equals(type);
    }*/

	public boolean notAtPlayThanks() {
		return !Interaction.PlayThanks.equals(type);
	}

	@JsonIgnore
	public boolean isEmptyBookmark() {
		return type == null && chapterIndex == null && lessonIndex == null;
	}

	@Override
	public int compareTo(BookMark other) {
		int chapterComparison = compare(this.chapterIndex, other.chapterIndex);
		if (chapterComparison != 0) return chapterComparison;
		int lessonCompare = compare(this.lessonIndex, other.lessonIndex);
		if(this.type!=null&&other.type!=null&&lessonCompare==0)
			return compare(BookMarkTypeEnum.valueOf(this.type).getIndex(), BookMarkTypeEnum.valueOf(other.type).getIndex());
		return lessonCompare;
	}
}
