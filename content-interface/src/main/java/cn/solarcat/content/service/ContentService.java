package cn.solarcat.content.service;

import java.util.List;

import cn.solarcat.common.pojo.EasyUIDataGridResult;
import cn.solarcat.common.util.SolarCatResult;
import cn.solarcat.pojo.TbContent;

public interface ContentService {

	SolarCatResult addContent(TbContent content);

	List<TbContent> getContentListByCid(long cid);

	EasyUIDataGridResult getContentByCatId(int categoryId, int page, int rows);
}
