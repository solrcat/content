package cn.solarcat.content.service.Impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.solarcat.aop.Log;
import cn.solarcat.common.configuration.ContentConfiguration;
import cn.solarcat.common.pojo.ACTION;
import cn.solarcat.common.pojo.EasyUIDataGridResult;
import cn.solarcat.common.pojo.LEVEL;
import cn.solarcat.common.util.SolarCatResult;
import cn.solarcat.content.service.ContentService;
import cn.solarcat.mapper.TbContentMapper;
import cn.solarcat.pojo.TbContent;
import cn.solarcat.pojo.TbContentExample;
import cn.solarcat.pojo.TbContentExample.Criteria;

/**
 * 内容管理Service
 * <p>
 * Title: ContentServiceImpl
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: www.itcast.cn
 * </p>
 * 
 * @version 1.0
 */
@Service
@Component
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Override
	@Log(action = ACTION.ADD, level = LEVEL.SERVICE)
	public SolarCatResult addContent(TbContent content) {
		// 将内容数据插入到内容表
		content.setCreated(new Date());
		content.setUpdated(new Date());
		// 插入到数据库
		contentMapper.insert(content);
		// 缓存同步,删除缓存中对应的数据。
		redisTemplate.opsForHash().delete(ContentConfiguration.CONTENT_LIST, content.getCategoryId().toString());
		return SolarCatResult.ok();
	}

	/**
	 * 根据内容分类id查询内容列表
	 * <p>
	 * Title: getContentListByCid
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param cid
	 * @return
	 * @see cn.content.service.ContentService#getContentListByCid(long)
	 */
	@Override
	@Log(action = ACTION.SELECT, level = LEVEL.SERVICE)
	public List<TbContent> getContentListByCid(long cid) {
		try {
			// 如果缓存中有直接响应结果
			String json = (String) redisTemplate.opsForHash().get(ContentConfiguration.CONTENT_LIST, cid + "");
			if (StringUtils.isNotBlank(json)) {
				// List<TbContent> list =JsonUtils.jsonToList(json, TbContent.class);
				List<TbContent> list = JSONObject.parseArray(json, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 如果没有查询数据库
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		// 设置查询条件
		criteria.andCategoryIdEqualTo(cid);
		// 执行查询
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
		try {
			redisTemplate.opsForHash().put(ContentConfiguration.CONTENT_LIST, cid + "",
					/* JsonUtils.objectToJson(list) */JSONObject.toJSONString(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	@Log(action = ACTION.ADD, level = LEVEL.SERVICE)
	public EasyUIDataGridResult getContentByCatId(int categoryId, int page, int rows) {
		// 设置分页信息
		PageHelper.startPage(page, rows);
		// 执行查询
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo((long) categoryId);
		List<TbContent> list = contentMapper.selectByExample(example);
		// 创建一个返回值对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		// 取分页结果
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		// 取总记录数
		long total = pageInfo.getTotal();
		result.setTotal((int) total);
		return result;
	}
}
