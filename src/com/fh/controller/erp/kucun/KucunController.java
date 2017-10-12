package com.fh.controller.erp.kucun;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.service.erp.goods.GoodsManager;
import com.fh.service.erp.spbrand.SpbrandManager;
import com.fh.service.erp.sptype.SptypeManager;
import com.fh.service.erp.spunit.SpunitManager;
import com.fh.util.Jurisdiction;
import com.fh.util.PageData;
import com.fh.util.Tools;

/** 
 * 说明：商品库存
 */
@Controller
@RequestMapping(value="/kucun")
public class KucunController extends BaseController {

	String menuUrl = "kucun/list.do"; //菜单地址(权限用)
	@Resource(name="goodsService")
	private GoodsManager goodsService;
	@Resource(name="spbrandService")
	private SpbrandManager spbrandService;
	@Resource(name="sptypeService")
	private SptypeManager sptypeService;
	@Resource(name="spunitService")
	private SpunitManager spunitService;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表Goods");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		pd.put("USERNAME", "admin".equals(Jurisdiction.getUsername())?"":Jurisdiction.getUsername());
		pd.put("isKucun", "yes");
		page.setPd(pd);
		List<PageData>	varList = goodsService.list(page);			//列出Goods列表
		List<PageData> spbrandList = spbrandService.listAll(Jurisdiction.getUsername()); 	//品牌列表
		List<PageData> sptypeList = sptypeService.listAll(Jurisdiction.getUsername()); 		//类别列表
		List<PageData> spunitList = spunitService.listAll(Jurisdiction.getUsername()); 		//计量单位列表
		mv.setViewName("erp/kucun/kucun_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("spbrandList", spbrandList);
		mv.addObject("sptypeList", sptypeList);
		mv.addObject("spunitList", spunitList);
		mv.addObject("QX",Jurisdiction.getHC());	//按钮权限
		return mv;
	}
	
	/**库存盘点
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/kucunchar")
	public ModelAndView kucunchar() throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String USERNAME = pd.getString("USERNAME");
		USERNAME = Tools.notEmpty(USERNAME)?USERNAME:Jurisdiction.getUsername();
		pd.put("USERNAME", USERNAME);
		List<PageData> goodsList = goodsService.listAll(pd);
		String[] color = {"AFD8F8","F6BD0F","8BBA00","FF8E46","008E8E","D64646","8E468E","588526","B3AA00","008ED6","9D080D","A186BE"};
	 	String strXML = "<graph caption='商品库存盘点' xAxisName='商品名' yAxisName='库存' decimalPrecision='0' formatNumberScale='0' baseFontSize='13'>";
	 	Random rand = new Random();
	 	for(int i=0;i<goodsList.size();i++){
	 		PageData goodsPd = goodsList.get(i);
	 		strXML += "<set name='"+goodsPd.getString("TITLE")+"' value='"+goodsPd.get("ZCOUNT").toString()+"' color='"+color[rand.nextInt(11)]+"'/>";
	 	}
	 	mv.addObject("strXML", strXML+"</graph>");
		mv.setViewName("erp/kucun/kucun_char");
		return mv;
	}
	
}
