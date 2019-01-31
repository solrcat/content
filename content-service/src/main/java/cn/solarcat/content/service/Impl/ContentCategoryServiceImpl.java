package cn.solarcat.content.service.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;

import cn.solarcat.aop.Log;
import cn.solarcat.common.pojo.ACTION;
import cn.solarcat.common.pojo.EasyUITreeNode;
import cn.solarcat.common.pojo.LEVEL;
import cn.solarcat.common.util.ReturnCode;
import cn.solarcat.common.util.SolarCatResult;
import cn.solarcat.content.service.ContentCategoryService;
import cn.solarcat.mapper.TbContentCategoryMapper;
import cn.solarcat.pojo.TbContentCategory;
import cn.solarcat.pojo.TbContentCategoryExample;
import cn.solarcat.pojo.TbContentCategoryExample.Criteria;

/**
 * 内容分类管理Service
 * <p>
 * Title: ContentCategoryServiceImpl
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
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;

	@Override
	@Log(action = ACTION.SELECT, level = LEVEL.SERVICE)
	public List<EasyUITreeNode> getContentCatList(long parentId) {
		// 根据parentid查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		// 设置查询条件
		criteria.andParentIdEqualTo(parentId);
		// 执行查询
		List<TbContentCategory> catList = contentCategoryMapper.selectByExample(example);
		// 转换成EasyUITreeNode的列表
		List<EasyUITreeNode> nodeList = new ArrayList<>();
		for (TbContentCategory tbContentCategory : catList) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent() ? "closed" : "open");
			// 添加到列表
			nodeList.add(node);
		}
		return nodeList;
	}

	@Override
	@Log(action = ACTION.ADD, level = LEVEL.SERVICE)
	public SolarCatResult addContentCategory(long parentId, String name) {
		// 创建一个tb_content_category表对应的pojo对象
		TbContentCategory contentCategory = new TbContentCategory();
		// 设置pojo的属性
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		// 1(正常),2(删除)
		contentCategory.setStatus(1);
		// 默认排序就是1
		contentCategory.setSortOrder(1);
		// 新添加的节点一定是叶子节点
		contentCategory.setIsParent(false);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		// 插入到数据库
		contentCategoryMapper.insert(contentCategory);
		// 判断父节点的isparent属性。如果不是true改为true
		// 根据parentid查询父节点
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if (!parent.getIsParent()) {
			parent.setIsParent(true);
			// 更新到数数据库
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		// 返回结果，返回E3Result，包含pojo
		return SolarCatResult.ok(contentCategory);
	}

	@Override
	@Log(action = ACTION.UPDATE, level = LEVEL.SERVICE)
	public SolarCatResult updateContentCategory(Long id, String name) {
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		if (contentCategory != null) {
			contentCategory.setName(name);
			contentCategory.setUpdated(new Date());
		} else {
			return SolarCatResult.build(ReturnCode.C250);
		}
		contentCategory.setName(name);
		if (contentCategoryMapper.updateByPrimaryKey(contentCategory) == 1) {
			return SolarCatResult.build(ReturnCode.C200);
		} else {
			return SolarCatResult.build(ReturnCode.C300);
		}
	}

	@Override
	@Log(action = ACTION.DELETE, level = LEVEL.SERVICE)
	public SolarCatResult deleteContentCategory(Long id) {
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		if (contentCategory != null && contentCategoryMapper.deleteByPrimaryKey(id) == 1) {
			return SolarCatResult.build(ReturnCode.C200);
		} else {
			return SolarCatResult.build(ReturnCode.C301);
		}
	}

}