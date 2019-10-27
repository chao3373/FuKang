package com.shenke.controller.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.shenke.service.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shenke.entity.Clerk;
import com.shenke.entity.Client;
import com.shenke.entity.Log;
import com.shenke.entity.SaleList;
import com.shenke.entity.SaleListProduct;
import com.shenke.entity.Sell;
import com.shenke.util.DateUtil;
import com.shenke.util.StringUtil;

/**
 * 销售订单导入Controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/admin/saleList")
public class SaleListAdminController {

    @Resource
    private SaleListService saleListService;

    @Resource
    private UserService userService;

    @Resource
    private SaleListProductService saleListProductService;

    @Autowired
    private StorageService storageService;

    @Resource
    private LogService logService;

    /**
     * 获取销售单号
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/genCode")
    public String genCode() throws Exception {
        StringBuffer code = new StringBuffer("XS");
        code.append(DateUtil.getCurrentDateStr());
        String saleNumber = saleListService.getTodayMaxSaleNumber();
        if (saleNumber != null) {
            code.append(StringUtil.formatCode(saleNumber));
        } else {
            code.append("0001");
        }
        return code.toString();
    }

    /**
     * 添加销售单 以及所有销售单商品
     *
     * @param
     * @param goodsJson
     * @return
     * @throws Exception
     */
    @RequestMapping("/save")
    @RequiresPermissions(value = "销售订货单")
    public Map<String, Object> save(String saleDate, String saleNumber, Integer clientId, Integer sellId,
                                    Integer clerkId, String lankman, String tel, String address, String deliveryDate, String goodsJson, Double zongjine)
            throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // SaleList数据封装
        SaleList saleList = new SaleList();
        saleList.setSaleDate(sdf.parse(saleDate));
        if (clerkId != null) {
            Clerk clerk = new Clerk();
            clerk.setId(clerkId);
            saleList.setClerk(clerk);
        }
        if (sellId != null) {
            {
                Sell sell = new Sell();
                sell.setId(sellId);
                saleList.setSell(sell);
            }
        }
        Client client = new Client();
        client.setId(clientId);
        saleList.setClient(client);
//        saleList.setTotalPrice(zongjine);
        saleList.setLankman(lankman);
        saleList.setTel(tel);
        saleList.setAddress(address);
        if (StringUtil.isNotEmpty(deliveryDate)) {
            saleList.setDeliveryDate(sdf.parse(deliveryDate));
        }
        saleList.setSaleNumber(saleNumber);

        saleList.setUser(userService.findByUserName((String) SecurityUtils.getSubject().getPrincipal()));// 设置操作用户

        Gson gson = new Gson();
        List<SaleListProduct> plgList = gson.fromJson(goodsJson, new TypeToken<List<SaleListProduct>>() {
        }.getType());
        for (SaleListProduct saleListProduct : plgList) {
            saleListProduct.setSaleList(saleList);
            saleListProduct.setState("下单");
            saleListProduct.setDaBaoShu(1);
            saleListProduct.setStorageid(0);
            saleListProduct.setLingshou(false);
            saleListProduct.setLevel(0);
        }

        saleListService.saveOne(saleList);
        saleListProductService.saveList(plgList);
        logService.save(new Log(Log.ADD_ACTION, "添加销售单"));
        map.put("success", true);
        return map;
    }

    /**
     * 查询所有含有未审核的销售单
     * @return
     * @throws ParseException
     */
    @RequestMapping("/list")
    public Map<String, Object> list(String saleNumber, String clientId, String clerkId, String bSaleDate,
                                    String eSaleDate) throws ParseException {

        SaleList saleList = new SaleList();

        if (StringUtil.isNotEmpty(saleNumber)) {
            saleList.setSaleNumber(saleNumber);
        }

        if (StringUtil.isNotEmpty(clientId)) {
            Client client = new Client();
            client.setId(Integer.parseInt(clientId));
            saleList.setClient(client);
        }

        if (StringUtil.isNotEmpty(clerkId)) {
            Clerk clerk = new Clerk();
            clerk.setId(Integer.parseInt(clerkId));
            saleList.setClerk(clerk);
        }

        if (eSaleDate != null && bSaleDate != null) {

            // 字符串转换成日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date eDate = sdf.parse(eSaleDate);
            Date bDate = sdf.parse(bSaleDate);

            // 给对象添加条件
            saleList.setbSaleDate(bDate);
            saleList.seteSaleDate(eDate);

        }

        System.out.println(saleList);
        Map<String, Object> map = new HashMap<String, Object>();
        List<SaleList> list = saleListService.list(saleList, Direction.DESC, "saleDate");
        System.out.println(list);
        map.put("rows", list);
        return map;
    }

    /**
     * 根据销售单id查询所有销售单的商品
     *
     * @return
     */
    @RequestMapping("/listProduct")
    public String listGoods(Integer saleListId) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (saleListId == null) {
            return null;
        }
        List<SaleListProduct> saleListProducts = saleListProductService.listBySaleListId(saleListId);
        System.out.println(saleListProducts);
        System.out.println(saleListProducts.size());
        map.put("rows", saleListProducts);
        System.out.println(map);
//        return map;
        JSON parse = JSONUtil.parse(map);
        String s = parse.toString();
        System.out.println(s);
        return s;
    }

    /**
     * 根据id删除订单信息和订单商品信息
     */
    @RequestMapping("/delete")
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> map = new HashMap<String, Object>();
        boolean bySaleListId = storageService.findBySaleListId(id);
        if (bySaleListId){
            saleListProductService.deleteBySaleListId(id);
            saleListService.deleteByid(id);
        } else {
            map.put("msg", "已经存在生产完成的商品，无法删除！");
        }
        map.put("success", bySaleListId);
        return map;
    }

    /**
     * 根据订单状态查询所有订单商品信息
     *
     * @param state
     * @return
     */
    @RequestMapping("/listProductByState")
    public Map<String, Object> listProductByState(Integer id, String state) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (id == null) {
            map.put("rows", saleListService.listProductByState(state));
        } else {
            map.put("rows", saleListService.listProductByStateAndId(id, state));
        }
        map.put("success", true);
        return map;
    }

    /**
     * 查询该订单中所有未审核和审核失败的订单
     *
     * @param id
     * @return
     */
    @RequestMapping("/getSaleListNo")
    public Map<String, Object> getSaleListNo(int id) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Integer> saleListNo = saleListService.getSaleListNo(id);
        map.put("success", true);
        map.put("arr", saleListNo);
        return map;
    }

    /**
     * 修改订单中的信息
     *
     * @param
     * @return
     * @throws ParseException
     */
    @RequestMapping("/saveTwo")
    public Map<String, Object> saveTwo(SaleList saleList, String saleDateq, String deliveryDateq) throws ParseException {
        saleList.setSaleDate(new SimpleDateFormat("yyyy-MM-dd").parse(saleDateq));
        saleList.setDeliveryDate(new SimpleDateFormat("yyyy-MM-dd").parse(deliveryDateq));
        if (StringUtil.isNotEmpty(deliveryDateq)) {
        }
        System.out.println(saleDateq);
        System.out.println(deliveryDateq);
        System.out.println(saleList);
        Map<String, Object> resultMap = new HashMap<>();

        saleListService.saveTwo(saleList);

        resultMap.put("success", true);
        System.out.println(resultMap);
        return resultMap;
    }

    @RequestMapping("/add")
    public Map<String, Object> add(SaleListProduct saleListProduct) {
        Map<String, Object> map = new HashMap<>();
        Integer id = saleListProduct.getId();
        SaleList saleList = saleListService.findById(id);
        SaleListProduct newSaleListProduct = new SaleListProduct();
        newSaleListProduct.setState("未审核");
        newSaleListProduct.setNum(saleListProduct.getNum());
        newSaleListProduct.setName(saleListProduct.getName());
        if (saleListProduct.getOneweight() == null) {
            Double oneweight = 0.93 * saleListProduct.getLength() * saleListProduct.getModel() * saleListProduct.getMeter();
            newSaleListProduct.setOneweight(oneweight);
            newSaleListProduct.setSumwight(oneweight * saleListProduct.getNum());
        } else {
            newSaleListProduct.setOneweight(saleListProduct.getOneweight());
            newSaleListProduct.setSumwight(saleListProduct.getNum() * saleListProduct.getOneweight());
        }
        newSaleListProduct.setLength(saleListProduct.getLength());
        newSaleListProduct.setStorageid(0);
        newSaleListProduct.setModel(saleListProduct.getModel());
        newSaleListProduct.setDaBaoShu(1);
        newSaleListProduct.setDemand(saleListProduct.getDemand());
        newSaleListProduct.setPrice(saleListProduct.getPrice());
        newSaleListProduct.setMeter(saleListProduct.getMeter());
        newSaleListProduct.setColor(saleListProduct.getColor());
        newSaleListProduct.setPeasant(saleListProduct.getPeasant());
        newSaleListProduct.setSaleList(saleList);
        newSaleListProduct.setClientname(saleList.getClient().getName());
        newSaleListProduct.setLevel(0);
        System.out.println(saleListProduct);
        System.out.println(newSaleListProduct);
        saleListProductService.save(newSaleListProduct);
        map.put("success", true);
        return map;
    }

    /***
     * 修改订金
     * @param dingjin
     * @return
     */
    @RequestMapping("/updateDingjin")
    public Map<String, Object> updateDingjin(Integer id, Double dingjin){
        Map<String, Object> map = new HashMap<>();
        saleListService.updateDingjin(dingjin, id);
        map.put("success", true);
        return map;
    }

}
