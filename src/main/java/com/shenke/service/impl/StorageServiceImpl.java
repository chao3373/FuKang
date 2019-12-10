package com.shenke.service.impl;

import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;

import com.shenke.entity.*;
import com.shenke.repository.*;
import com.shenke.service.ClientService;
import com.shenke.util.*;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shenke.service.StorageService;

/**
 * 入库单Service实现类
 *
 * @author Administrator
 */
@Service("storageService")
@Transactional
public class StorageServiceImpl implements StorageService {

    @Resource
    private StorageRepository storageRepository;

    @Resource
    private SaleListRepository saleListRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private SaleListProductRepository saleListProductRepository;

    @Resource
    private JitaiProductionAllotRepository jitaiProductionAllotRepository;

    @Resource
    private JiTaiRepository jiTaiRepository;

    @Resource
    private ClerkRepository clerkRepository;

    @Resource
    private ClientService clientService;

    @Resource
    private GroupRepository groupRepository;

    @Override
    public void add(Storage storage, String clerkName, String groupName) {
        System.out.println(storage);
        System.out.println(storage.getPrice());
        Group group = storage.getJiTai().getGroup();
        SaleListProduct saleListProduct = saleListProductRepository.findOne(storage.getSaleListProduct().getId());
        Clerk clerk = storage.getJiTai().getClerk();
        Double realityweight = storage.getRealityweight();

        System.out.println(saleListProduct.getUnitPrice());
        BeanUtils.copyProperties(saleListProduct, storage);

        System.out.println(storage.getUnitPrice());
        storage.setId(null);
        storage.setClerk(clerk);
        storage.setGroup(group);
        storage.setRealityweight(realityweight);
        storage.setShengyulength(storage.getLength() * storage.getDabaonum());
        storage.setDateInProduced(new Date(System.currentTimeMillis()));
        storage.setSaleNumber(saleListProduct.getSaleList().getSaleNumber());
        storage.setState("生产完成:" + storage.getJiTai().getName());
        storage.setJiTaiName(storage.getJiTai().getName());
        storage.setClerkName(clerk.getName());
        storage.setPrice(storage.getSaleListProduct().getPrice());
        storage.setGroup(storage.getJiTai().getGroup());
        storage.setGroupName(storage.getJiTai().getGroup().getName());
        storage.setPingfang(storage.getLength() * storage.getModel());

        storage.setCode(saleListProduct.getCode());
        System.out.println(storage);
        System.out.println(storage.getPrice());
        storageRepository.save(storage);

    }

    @Override
    public void add(Storage storage, String clerkName, String groupName, Double changdu, String type) {

        System.out.println(storage);
        Group group = groupRepository.findByGrouptName(groupName);
        SaleListProduct saleListProduct = saleListProductRepository.findOne(storage.getSaleListProduct().getId());
        Clerk clerk = storage.getJiTai().getClerk();
        Double realityweight = storage.getRealityweight();
        System.out.println(saleListProduct.getUnitPrice());
        BeanUtils.copyProperties(saleListProduct, storage);
        if (StringUtil.isNotEmpty(type) && type.equals("保存不添加完成数")) {
            storage.setSaleListProduct(null);
        }
        System.out.println(storage.getUnitPrice());
        storage.setId(null);
        System.out.println(clerk);
        storage.setClerk(clerk);
        storage.setGroup(group);
        storage.setRealityweight(realityweight);
        storage.setDateInProduced(new Date(System.currentTimeMillis()));
        storage.setSaleNumber(saleListProduct.getSaleList().getSaleNumber());
        storage.setState("生产完成:" + storage.getJiTai().getName());
        storage.setLength(changdu);
        storage.setJiTaiName(storage.getJiTai().getName());
        storage.setClerkName(clerk.getName());
        storage.setShengyulength(changdu * storage.getDabaonum());
        storage.setGroup(storage.getJiTai().getGroup());
        storage.setGroupName(storage.getJiTai().getGroup().getName());
        storage.setPrice(storage.getPrice());
        storage.setPingfang(storage.getLength() * storage.getModel());

        storage.setCode(saleListProduct.getCode());
        System.out.println(storage);
        storageRepository.save(storage);
    }

    @Override
    public void feibiaoAdd(Storage storage, String clerkName, String groupName) {

        Group group = groupRepository.findByGrouptName(groupName);
        SaleListProduct saleListProduct = saleListProductRepository.findOne(storage.getSaleListProduct().getId());
        Clerk clerk = clerkRepository.findByNam(clerkName);
        Double realityweight = storage.getRealityweight();

        BeanUtils.copyProperties(saleListProduct, storage);
        storage.setId(null);
        storage.setClerk(clerk);
        storage.setGroup(group);
        storage.setRealityweight(realityweight);
        storage.setDateInProduced(new Date(System.currentTimeMillis()));
        storage.setState("生产完成:" + storage.getJiTai().getName());
        storage.setJiTaiName(storage.getJiTai().getName());
        storage.setClerkName(clerkName);
        storage.setGroup(storage.getJiTai().getGroup());
        storage.setGroupName(storage.getJiTai().getGroup().getName());
        storage.setSaleListProduct(null);
        storageRepository.save(storage);

    }

    @Override
    public List<Storage> fandAll() {
        return storageRepository.findAll();
    }

    @Override
    public List<Storage> fandAllBySerialNumber(String serialNumber, String state) {
        return storageRepository.fandAllBySerialNumber(serialNumber, state);
    }

    @Override
    public Storage selectByMaxId() {
        return storageRepository.selectByMaxId();
    }

    @Override
    public void outStorage(String[] id, Date date) {
        // TODO Auto-generated method stub
        storageRepository.outStorage(id, date);
    }

    @Override
    public List<Storage> outSuccess() {
        // TODO Auto-generated method stub
        return storageRepository.outSuccess();
    }


    @Override
    public void gai(String storage_number, int id) {
        // TODO Auto-generated method stub
        storageRepository.gai(storage_number, id);
    }

    @Override
    public void updateStateById(String state, String[] id, Date date, String ck) {
        storageRepository.updateStateById(state, id, date, ck);
//        Storage one = storageRepository.findOne(id);
//        Integer id1 = one.getSaleListProduct().getId();

    }

    @Override
    public List<Object[]> findByClientAndGroupByName(String client) {
        List<Object[]> byClientAndGroupByName = storageRepository.findByClientAndGroupByName(client);
        return byClientAndGroupByName;
    }

    @Override
    public List<Storage> findAll() {
        return storageRepository.findAll();
    }

    @Override
    public Storage findById(Integer s) {
        return storageRepository.findOne(s);
    }

    @Override
    public List<JieSuan> FindByGroup(String client) {
        System.out.println(client);
        System.out.println(storageRepository.FindByGroup(client));
        List<JieSuan> castEntity = EntityUtils.castEntity(storageRepository.FindByGroup(client), JieSuan.class);
        return castEntity;
    }

    @Override
    public List<Storage> searchLiftMoney(Map<String, Object> map) {
        System.out.println(map.get("productDatee"));
        return storageRepository.findAll(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtil.isNotEmpty((String) map.get("saleNumber"))) {
                    predicate.getExpressions().add(cb.equal(root.get("saleNumber"), (String) map.get("saleNumber")));
                }
                if (StringUtil.isNotEmpty((String) map.get("productDate")) && !((String) map.get("productDatee")).equals("null")) {
                    try {
                        System.out.println(map.get("productDate"));
                        predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), new SimpleDateFormat("yyy-MM-dd HH:mm:ss").parse((String) map.get("productDate") + " 00:00:00")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (StringUtil.isNotEmpty((String) map.get("productDatee")) && !((String) map.get("productDatee")).equals("null")) {
                    try {
                        System.out.println(map.get("productDatee"));
                        predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), new SimpleDateFormat("yyy-MM-dd HH:mm:ss").parse((String) map.get("productDatee") + " 23:59:59")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (StringUtil.isNotEmpty((String) map.get("peasant"))) {
                    predicate.getExpressions().add(cb.equal(root.get("peasant"), map.get("peasant")));
                }
                if (StringUtil.isNotEmpty((String) map.get("realityweight"))) {
                    predicate.getExpressions().add(cb.equal(root.get("realityweight"), map.get("realityweight")));
                }
                if (StringUtil.isNotEmpty((String) map.get("name"))) {
                    predicate.getExpressions().add(cb.equal(root.get("name"), map.get("name")));
                }
                if (StringUtil.isNotEmpty((String) map.get("client"))) {
                    predicate.getExpressions().add(cb.equal(root.get("clientname"), map.get("client")));
                }
                if (StringUtil.isNotEmpty((String) map.get("mode"))) {
                    predicate.getExpressions().add(cb.equal(root.get("model"), map.get("mode")));
                }
                if (StringUtil.isNotEmpty((String) map.get("price"))) {
                    predicate.getExpressions().add(cb.equal(root.get("price"), map.get("price")));
                }
                Subquery subQuery = query.subquery(String.class);

                Root from = subQuery.from(Storage.class);
                subQuery.select(from.get("id")).where(cb.like(from.get("state"), "%生产完成%"));

                predicate.getExpressions().add(cb.or(root.get("id").in(subQuery)));

                return predicate;
            }
        });
    }

    @Override
    public void setLocation(Integer parseInt, Integer location) {
        storageRepository.setLocation(parseInt, location);
    }

    @Override
    public void save(Storage storage) {
        storage.setShengyulength(storage.getLength());
        storageRepository.save(storage);
    }

    @Override
    public void save(Storage storage, Integer num) {
        if (num != null) {
            for (int i = 0; i < num; i++) {
                Storage storage1 = new Storage();
                storage1.setName(storage.getName());
                storage1.setModel(storage.getModel());
                storage1.setPrice(storage.getPrice());
                storage1.setLength(storage.getLength());
                storage1.setColor(storage.getColor());
                storage1.setWeight(storage.getWeight());
                storage1.setDao(storage.getDao());
                storage1.setBrand(storage.getBrand());
                storage1.setPack(storage.getPack());
                storage1.setLetter(storage.getLetter());
                storage1.setPatu(storage.getPatu());
                storage1.setMeter(storage.getMeter());
                storage1.setClientname(storage.getClientname());
                storage1.setState(storage.getState());
                storage1.setLocation(storage.getLocation());
                storageRepository.save(storage1);
            }
        }
    }

    @Override
    public List<Storage> findByState(String state) {
        return storageRepository.findByState(state);
    }

    @Override
    public List<Storage> detail(Map<String, Object> map) {
        if (map.get("order") != null && map.get("order") != "") {
            return storageRepository.findAll(new Specification<Storage>() {
                public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Predicate predicate = cb.conjunction();
                    if (StringUtil.isNotEmpty((String) map.get("date"))) {
                        String date = (String) map.get("date");
                        try {
                            String st = date + " 00:00:00";
                            String ed = date + " 23:59:59";
                            System.out.println(st);
                            System.out.println(ed);
                            Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(st);
                            Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ed);
                            System.out.println(star);
                            System.out.println(end);
                            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("deliveryTime"), star));
                            predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("deliveryTime"), end));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (StringUtil.isNotEmpty((String) map.get("client"))) {
                        predicate.getExpressions().add(cb.like(root.get("clientname"), "%" + map.get("client") + "%"));
                    }
                    if (StringUtil.isNotEmpty((String) map.get("peasant"))) {
                        predicate.getExpressions().add(cb.like(root.get("peasant"), "%" + map.get("peasant") + "%"));
                    }
                    if (StringUtil.isNotEmpty((String) map.get("product"))) {
                        predicate.getExpressions().add(cb.like(root.get("name"), "%" + map.get("product") + "%"));
                    }
                    if (StringUtil.isNotEmpty((String) map.get("chukudanhao"))) {
                        predicate.getExpressions().add(cb.equal(root.get("outNumber"), map.get("chukudanhao")));
                    }
                    predicate.getExpressions().add(cb.like(root.get("state"), "%装车%"));

                    query.groupBy(root.get("saleListProduct").get("id"), root.get("name"), root.get("model"), root.get("price"), root.get("length"), root.get("color"), root.get("realityweight"), root.get("dao"), root.get("peasant"), root.get("clientname"), root.get("outNumber"), root.get("dabaonum"), root.get("unitPrice"));
                    System.out.println("执行");
                    return predicate;
                }
            }, new Sort(Sort.Direction.ASC, "name", "peasant"));
//            new Sort(Sort.Direction.ASC, "peasant", "outNumber", "name", "model", "length", "price")
        }
        return storageRepository.findAll(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtil.isNotEmpty((String) map.get("date"))) {
                    String date = (String) map.get("date");
                    try {
                        String st = date + " 00:00:00";
                        String ed = date + " 23:59:59";
                        System.out.println(st);
                        System.out.println(ed);
                        Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(st);
                        Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ed);
                        System.out.println(star);
                        System.out.println(end);
                        predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("deliveryTime"), star));
                        predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("deliveryTime"), end));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (StringUtil.isNotEmpty((String) map.get("client"))) {
                    predicate.getExpressions().add(cb.like(root.get("clientname"), "%" + map.get("client") + "%"));
                }
                if (StringUtil.isNotEmpty((String) map.get("peasant"))) {
                    predicate.getExpressions().add(cb.like(root.get("peasant"), "%" + map.get("peasant") + "%"));
                }
                if (StringUtil.isNotEmpty((String) map.get("product"))) {
                    predicate.getExpressions().add(cb.like(root.get("name"), "%" + map.get("product") + "%"));
                }

                if (StringUtil.isNotEmpty((String) map.get("chukudanhao"))) {
                    predicate.getExpressions().add(cb.equal(root.get("outNumber"), map.get("chukudanhao")));
                }
                predicate.getExpressions().add(cb.like(root.get("state"), "%装车%"));

                query.groupBy(root.get("saleListProduct").get("id"), root.get("name"), root.get("model"), root.get("price"), root.get("length"), root.get("color"), root.get("realityweight"), root.get("dao"), root.get("peasant"), root.get("clientname"), root.get("outNumber"), root.get("dabaonum"));

                return predicate;
            }
        }, new Sort(Sort.Direction.ASC, "name", "peasant"));
    }


    @Override
    public List<Storage> selectClientNameByOutDate(Date s) {
        return storageRepository.selectClientNameByOutDate(s);
    }

    @Override
    public List<Storage> selectOutByOutNumber(String outNumber) {
        return storageRepository.selectOutByOutNumber(outNumber);
    }

    @Override
    public String selectCountByNameAndOutNumber(String name, String outNumber) {
        return storageRepository.selectCountByNameAndOutNumber(name, outNumber);
    }

    /***
     *
     * 根据
     * @param
     * @return
     */
    @Override
    public List<Count> FindBySaleListId() {
        List<Count> cast = EntityUtils.castEntity(storageRepository.FindBySaleListId(), Count.class);
        return cast;
    }

    @Override
    public Integer countBySaleListProductId(Integer id, Storage storage, String state, String dateInProducedd, String dateInProduceddd) {
        Long count = storageRepository.count(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicates = cb.conjunction();
                if (id != null && id != 0) {
                    predicates.getExpressions().add(cb.equal(root.get("saleListProduct").get("id"), id));
                    predicates.getExpressions().add(cb.equal(root.get("name"), storage.getName()));
                    predicates.getExpressions().add(cb.equal(root.get("model"), storage.getModel()));
                    predicates.getExpressions().add(cb.equal(root.get("price"), storage.getPrice()));
                    predicates.getExpressions().add(cb.equal(root.get("length"), storage.getLength()));
                    predicates.getExpressions().add(cb.equal(root.get("color"), storage.getColor()));
                    predicates.getExpressions().add(cb.equal(root.get("realityweight"), storage.getRealityweight()));
                    predicates.getExpressions().add(cb.equal(root.get("dao"), storage.getDao()));
                    predicates.getExpressions().add(cb.equal(root.get("peasant"), storage.getPeasant()));
                    predicates.getExpressions().add(cb.equal(root.get("clientname"), storage.getClientname()));
                    predicates.getExpressions().add(cb.like(root.get("state"), state));

//                    predicates.getExpressions().add(cb.equal(root.get("groupName"), storage.getGroupName()));
//                    predicates.getExpressions().add(cb.equal(root.get("clerkName"), storage.getClerkName()));
                }
                if (StringUtil.isNotEmpty(dateInProducedd) && StringUtil.isNotEmpty(dateInProduceddd)) {
                    try {
                        Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd);
                        Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProduceddd);
                        System.out.println("开始时间：" + star);
                        System.out.println("结束时间：" + end);
                        predicates.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                        predicates.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
                        query.groupBy(root.get("dateInProduced"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                query.groupBy(root.get("saleListProduct").get("id"), root.get("name"), root.get("model"), root.get("price"), root.get("length"), root.get("color"), root.get("realityweight"), root.get("dao"), root.get("peasant"), root.get("clientname"));
                return predicates;
            }
        });
        return count.intValue();
    }

    public List<Storage> findSaleListNumber() {
        return storageRepository.findSaleListNumber();
    }

    @Override
    public List<Storage> findStorage(String saleNumber) {
        return storageRepository.findStorage(saleNumber);
    }


    @Override
    public List<Storage> KucunSearch(Map<String, Object> map) {
        return storageRepository.findAll(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtil.isNotEmpty((String) map.get("saleDate"))) {
                    try {
                        predicate.getExpressions().add(cb.equal(root.get("saleList").get("saleDate"), new SimpleDateFormat("yyy-MM-dd").parse((String) map.get("saleDate"))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (map.get("clientname") != null) {
                    predicate.getExpressions().add(cb.equal(root.get("clientname"), map.get("clientname")));
                }
                if ((map.get("saleNumber") != null)) {
                    predicate.getExpressions().add(cb.equal(root.get("saleNumber"), map.get("saleNumber")));
                }

                return predicate;
            }
        });
    }


    @Override
    public List<Storage> JitaiProduct(Storage storage, java.util.Date date, java.util.Date star, java.util.Date end) {
        return storageRepository.findAll(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                /*if (StringUtil.isNotEmpty(storage.getGroupName())) {
                    predicate.getExpressions().add(cb.equal(root.get("groupName"), storage.getGroupName()));
                }*/
                if (StringUtil.isNotEmpty(storage.getJiTaiName())) {
                    predicate.getExpressions().add(cb.equal(root.get("jiTaiName"), storage.getJiTaiName()));
                }
                /*if (StringUtil.isNotEmpty(storage.getGroupName())) {
                    predicate.getExpressions().add(cb.equal(root.get("groupName"), storage.getGroupName()));
                }*/
                if (StringUtil.isNotEmpty(storage.getClerkName())) {
                    predicate.getExpressions().add(cb.equal(root.get("clerkName"), storage.getClerkName()));
                }
                if (StringUtil.isNotEmpty(storage.getClientname())) {
                    predicate.getExpressions().add(cb.equal(root.get("clientname"), storage.getClientname()));
                }
                if (star != null) {
                    System.out.println("开始时间");
                    System.out.println(star);
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                }
                if (end != null) {
                    System.out.println("结束时间");
                    System.out.println(end);
                    predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
                }
                return predicate;
            }
        });
    }

    @Override
    public List<Storage> select(Storage storage, String dateInProducedd) {
        return storageRepository.findAll(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtil.isNotEmpty(storage.getSaleNumber())) {
                    predicate.getExpressions().add(cb.like(root.get("saleNumber"), "%" + storage.getSaleNumber() + "%"));
                }
                if (StringUtil.isNotEmpty(storage.getName())) {
                    predicate.getExpressions().add(cb.equal(root.get("name"), storage.getName()));
                }
                if (storage.getLocation() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("location").get("id"), storage.getLocation().getId()));
                }
                if (storage.getJiTai() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("jiTai").get("id"), storage.getJiTai().getId()));
                }if (storage.getId() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("id"), storage.getId()));
                }
                if (StringUtil.isNotEmpty(storage.getPeasant())) {
                    predicate.getExpressions().add(cb.equal(root.get("peasant"), storage.getPeasant()));
                }
                if (StringUtil.isNotEmpty(dateInProducedd) && storage.getGroup() != null) {
                    if (StringUtil.isNotEmpty(dateInProducedd) && !storage.getGroupName().equals("夜班")) {
                        System.out.println("白班");
                        try {
                            java.util.Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 00:00:00");
                            java.util.Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 23:59:59");
                            System.out.println("开始时间：" + star);
                            System.out.println("结束时间：" + end);
                            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                            predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("夜班");
                        String starr = dateInProducedd + " 17:00:00";
                        String endd = dateInProducedd.split("-")[0] + "-" + dateInProducedd.split("-")[1] + "-" + (Integer.parseInt(dateInProducedd.split("-")[2]) + 1) + " 14:00:00";
                        try {
                            Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(starr);
                            Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endd);
                            System.out.println("开始时间：" + star);
                            System.out.println("结束时间：" + end);
                            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                            predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    predicate.getExpressions().add(cb.equal(root.get("group"), storage.getGroup()));
                }

                if (StringUtil.isNotEmpty(dateInProducedd) && storage.getGroup() == null) {
                    try {
                        java.util.Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 00:00:00");
                        java.util.Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 23:59:59");
                        System.out.println("开始时间：" + star);
                        System.out.println("结束时间：" + end);
                        predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                        predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (storage.getClerk() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("clerk").get("id"), storage.getClerk().getId()));
                }
                if (StringUtil.isNotEmpty(storage.getClientname())) {
                    predicate.getExpressions().add(cb.equal(root.get("clientname"), clientService.findById(Integer.parseInt(storage.getClientname())).getName()));
                }
                if (storage.getLength() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("length"), storage.getLength()));
                }
                if (storage.getModel() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("model"), storage.getModel()));
                }
                if (StringUtil.isNotEmpty(storage.getPrice())) {
                    predicate.getExpressions().add(cb.equal(root.get("price"), storage.getPrice()));
                }
                if (storage.getRealityweight() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("realityweight"), storage.getRealityweight()));
                }
                if (StringUtil.isNotEmpty(storage.getState())) {
                    String state = storage.getState();
                    System.out.println(storage.getState());
                    if (storage.getState().startsWith("'")) {
                        state = storage.getState().substring(1, storage.getState().length() - 1);
                        System.out.println(state);
                    }
                    predicate.getExpressions().add(cb.like(root.get("state"), "%" + state + "%"));
                }
                if (StringUtil.isNotEmpty(storage.getColor())) {
                    predicate.getExpressions().add(cb.equal(root.get("color"), storage.getColor()));
                }
                predicate.getExpressions().add(cb.like(root.get("state"), "%生产完成%"));
                return predicate;
            }
        });
    }

    @Override
    public List<Storage> selectt(Storage storage, String dateInProducedd, String dateInProduceddd) {
        return storageRepository.findAll(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtil.isNotEmpty(storage.getSaleNumber())) {
                    predicate.getExpressions().add(cb.like(root.get("saleNumber"), "%" + storage.getSaleNumber() + "%"));
                }
                if (StringUtil.isNotEmpty(storage.getName())) {
                    predicate.getExpressions().add(cb.equal(root.get("name"), storage.getName()));
                }
                if (storage.getLocation() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("location").get("id"), storage.getLocation().getId()));
                }
                if (storage.getJiTai() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("jiTai").get("id"), storage.getJiTai().getId()));
                }
                if (StringUtil.isNotEmpty(storage.getPeasant())) {
                    predicate.getExpressions().add(cb.equal(root.get("peasant"), storage.getPeasant()));
                }
                if (StringUtil.isNotEmpty(dateInProducedd) && StringUtil.isNotEmpty(dateInProduceddd)) {
                    try {
                        Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd);
                        Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProduceddd);
                        System.out.println("开始时间：" + star);
                        predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                        System.out.println("结束时间：" + end);
                        predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
//                        query.groupBy(root.get("dateInProduced"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (storage.getClerk() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("clerk").get("id"), storage.getClerk().getId()));
                }
                if (StringUtil.isNotEmpty(storage.getClientname())) {
                    predicate.getExpressions().add(cb.equal(root.get("clientname"), clientService.findById(Integer.parseInt(storage.getClientname())).getName()));
                }
                if (storage.getLength() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("length"), storage.getLength()));
                }
                if (storage.getModel() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("model"), storage.getModel()));
                }
                if (StringUtil.isNotEmpty(storage.getPrice())) {
                    predicate.getExpressions().add(cb.equal(root.get("price"), storage.getPrice()));
                }
                if (storage.getRealityweight() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("realityweight"), storage.getRealityweight()));
                }
                if (StringUtil.isNotEmpty(storage.getState())) {
                    String state = storage.getState();
                    System.out.println(storage.getState());
                    if (storage.getState().startsWith("'")) {
                        state = storage.getState().substring(1, storage.getState().length() - 1);
                        System.out.println(state);
                    }
                    predicate.getExpressions().add(cb.like(root.get("state"), "%" + state + "%"));
                }
                if (StringUtil.isNotEmpty(storage.getColor())) {
                    predicate.getExpressions().add(cb.equal(root.get("color"), storage.getColor()));
                }
                predicate.getExpressions().add(cb.like(root.get("state"), "%生产完成%"));
//                query.groupBy(root.get("saleListProduct").get("id"), root.get("name"), root.get("model"), root.get("price"), root.get("length"), root.get("color"), root.get("realityweight"), root.get("dao"), root.get("peasant"), root.get("clientname"), root.get("dabaonum"), root.get("unitPrice"));
                query.groupBy(root.get("saleListProduct").get("id"), root.get("name"), root.get("model"), root.get("price"), root.get("length"), root.get("color"), root.get("realityweight"), root.get("peasant"), root.get("clientname"), root.get("dabaonum"), root.get("unitPrice"));
                return predicate;
            }
        }, new Sort(Sort.Direction.ASC, "peasant", "name", "model", "price", "length", "color", "realityweight"));
    }

    @Override
    public void updateByIdAndState(String[] parseInt, String state) {
        storageRepository.updateByIdAndState(parseInt, state);
    }

    @Override
    public List<Storage> selectByState(String state) {
        return storageRepository.selectByState("%" + state + "%");
    }

    @Override
    public void updateOutNumberById(int parseInt, String ck) {
        storageRepository.updateOutNumberById(ck, parseInt);
    }

    @Override
    public String genCode() throws Exception {
        StringBuffer code = new StringBuffer("CK");
        code.append(DateUtil.getCurrentDateStr());
        String saleNumber = storageRepository.getTodayMaxOutNumber();
        if (saleNumber != null) {
            code.append(StringUtil.formatCode(saleNumber));
        } else {
            code.append("00001");
        }
        return code.toString();
    }

    @Override
    public void updateClerk(Integer id, String clerkName, Integer clerkId) {
        storageRepository.updateClerk(id, clerkName, clerkId);
    }

    @Override
    public void editKuCun(Integer id, Integer oneWeight, Double shiji, Double length) {
        storageRepository.editKuCun(id, oneWeight, shiji, length);
    }

    @Override
    public List<Storage> findeBySaleNumberAndClient(String saleNumber, String client) {
        return storageRepository.findeBySaleNumberAndClient(saleNumber, client);
    }

    /***
     *
     * 按月查询报表
     * @param month
     * @param year
     * @return
     */
    @Override
    public Month selectMonth(String month, String year) {
        Month month1 = new Month();
        String m = Integer.parseInt(month) < 10 ? 0 + month : month;
        Integer month2 = Integer.parseInt(month) + 1;
        String m1 = month2 < 10 ? 0 + month2 + "" : month2 + "";
        String date = year + "-" + m + "-01";
        String date1 = year + "-" + m1 + "-01";
        try {
            java.util.Date udate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            java.util.Date endate = new SimpleDateFormat("yyyy-MM-dd").parse(date1);
            System.out.println(date);
            System.out.println(date1);
            Integer lastMonth = storageRepository.lastMonth(udate);
            Integer monthIn = storageRepository.monthIn(udate, endate);
            Integer monthOut = storageRepository.monthOut(udate, endate);
            Integer kuCun = lastMonth + monthIn - monthOut;
            month1.setLastMonth(lastMonth);
            month1.setMonthIn(monthIn);
            month1.setMonthOut(monthOut);
            month1.setKuCun(kuCun);
            return month1;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Month selectYear(String year) {
        Integer lastYear = Integer.parseInt(year) + 1;
        Month month = new Month();
        try {
            java.util.Date n = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-01-01");
            java.util.Date n1 = new SimpleDateFormat("yyyy-MM-dd").parse(lastYear + "-01-01");
            System.out.println(n);
            System.out.println(n1);
            Integer lastMonth = storageRepository.lastMonth(n);
            Integer monthIn = storageRepository.monthIn(n, n1);
            Integer monthOut = storageRepository.monthOut(n, n1);
            Integer kuCun = lastMonth + monthIn - monthOut;
            month.setLastMonth(lastMonth);
            month.setMonthIn(monthIn);
            month.setMonthOut(monthOut);
            month.setKuCun(kuCun);
            return month;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updatebanzu(Integer id, String banzu, Integer banzuid) {
        storageRepository.updatebanzu(id, banzu, banzuid);
    }

    /***
     * 根据id修改重量
     * @param id
     * @param zhongliang
     */
    @Override
    public void updatezhongliang(Integer id, Double zhongliang) {
        storageRepository.updatezhongliang(id, zhongliang);
    }

    /***
     * 根据id删除库存
     * @param id
     */
    @Override
    public String deletekucun(Integer id) {
        Storage storage = storageRepository.findOne(id);
        Integer saleListProductId = storage.getSaleListProduct().getId();
        SaleListProduct saleListProduct = saleListProductRepository.findOne(saleListProductId);
        storageRepository.deletekucun(id);
        //更新完成数量
        Integer count = storageRepository.getCountBySaleListProductId(saleListProductId);
        if (count == null) {
            count = 0;
        }
        saleListProduct.setAccomplishNumber(count);
//        saleListProductRepository.updateAccomplishNumber(saleListProductId);
        //根据id查询完成数跟总数量，完成数小于总数量则修改状态为下发机台
        if (saleListProduct.getNum() > saleListProduct.getAccomplishNumber()) {
            saleListProduct.setState("下发机台：" + saleListProduct.getJiTai().getName());
            saleListProductRepository.save(saleListProduct);
            return "修改成功，并修改状态为下发！";
        } else {
            saleListProductRepository.save(saleListProduct);
            return "修改成功完成数量不小于总数量不修改状态！";
        }
    }

    @Override
    public void updatechangdu(Integer changdu, Integer id) {
        storageRepository.updatechangdu(changdu, id);
    }

    /***
     * 根据id修改厚度
     * @param houdu
     * @param id
     */
    @Override
    public void updatehoudu(String houdu, Integer id) {
        storageRepository.updatehoudu(houdu, id);
    }

    //根据条件查询提货商品
    @Override
    public List<Storage> selectTihuo(String pandianji) {
        if (StringUtil.isEmpty(pandianji)) {
            return storageRepository.selectTihuo();
        }
        return storageRepository.selectTihuo(pandianji);
    }

    /***
     * 根据salelistproductid查询完成数量
     * @param id
     * @return
     */
    @Override
    public Integer findCountBySaleListProductId(Integer id) {
        return storageRepository.findCountBySaleListProductId(id);
    }

    /***
     * 修改库存页面查询
     * @param storage
     * @param dateInProducedd
     * @return
     */
    @Override
    public List<Storage> selectEdit(Storage storage, String dateInProducedd, Integer page, Integer rows) {
        Pageable pageable = new PageRequest(page - 1, rows);
        Page<Storage> storagePage = storageRepository.findAll(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtil.isNotEmpty(storage.getSaleNumber())) {
                    predicate.getExpressions().add(cb.like(root.get("saleNumber"), "%" + storage.getSaleNumber() + "%"));
                }
                if (storage.getLength() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("length"), storage.getLength()));
                }
                if (storage.getModel() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("model"), storage.getModel()));
                }
                if (storage.getPrice() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("price"), storage.getPrice()));
                }
                if (storage.getRealityweight() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("realityweight"), storage.getRealityweight()));
                }
                if (StringUtil.isNotEmpty(storage.getName())) {
                    predicate.getExpressions().add(cb.equal(root.get("name"), storage.getName()));
                }
                if (storage.getLocation() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("location").get("id"), storage.getLocation().getId()));
                }
                if (storage.getId() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("id"), storage.getId()));
                }
                if (storage.getJiTai() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("jiTai").get("id"), storage.getJiTai().getId()));
                }
                if (StringUtil.isNotEmpty(storage.getPeasant())) {
                    predicate.getExpressions().add(cb.equal(root.get("peasant"), storage.getPeasant()));
                }
                if (StringUtil.isNotEmpty(dateInProducedd) && storage.getGroup() != null) {
                    if (StringUtil.isNotEmpty(dateInProducedd) && !storage.getGroupName().equals("夜班")) {
                        System.out.println("白班");
                        try {
                            java.util.Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 00:00:00");
                            java.util.Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 23:59:59");
                            System.out.println("开始时间：" + star);
                            System.out.println("结束时间：" + end);
                            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                            predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("夜班");
                        String starr = dateInProducedd + " 17:00:00";
                        String endd = dateInProducedd.split("-")[0] + "-" + dateInProducedd.split("-")[1] + "-" + (Integer.parseInt(dateInProducedd.split("-")[2]) + 1) + " 14:00:00";
                        try {
                            Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(starr);
                            Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endd);
                            System.out.println("开始时间：" + star);
                            System.out.println("结束时间：" + end);
                            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                            predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    predicate.getExpressions().add(cb.equal(root.get("group"), storage.getGroup()));
                }

                if (StringUtil.isNotEmpty(dateInProducedd) && storage.getGroup() == null) {
                    try {
                        java.util.Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 00:00:00");
                        java.util.Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 23:59:59");
                        System.out.println("开始时间：" + star);
                        System.out.println("结束时间：" + end);
                        predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                        predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (storage.getClerk() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("clerk").get("id"), storage.getClerk().getId()));
                }
                if (StringUtil.isNotEmpty(storage.getClientname())) {
                    predicate.getExpressions().add(cb.equal(root.get("clientname"), clientService.findById(Integer.parseInt(storage.getClientname())).getName()));
                }
                if (storage.getLength() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("length"), storage.getLength()));
                }
                if (storage.getModel() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("model"), storage.getModel()));
                }
                if (StringUtil.isNotEmpty(storage.getPrice())) {
                    predicate.getExpressions().add(cb.equal(root.get("price"), storage.getPrice()));
                }
                if (storage.getRealityweight() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("realityweight"), storage.getRealityweight()));
                }
                if (StringUtil.isNotEmpty(storage.getState())) {
                    String state = storage.getState();
                    System.out.println(storage.getState());
                    if (storage.getState().startsWith("'")) {
                        state = storage.getState().substring(1, storage.getState().length() - 1);
                        System.out.println(state);
                    }
                    predicate.getExpressions().add(cb.like(root.get("state"), "%" + state + "%"));
                }
                if (StringUtil.isNotEmpty(storage.getColor())) {
                    predicate.getExpressions().add(cb.equal(root.get("color"), storage.getColor()));
                }
                return predicate;
            }
        }, pageable);
        return storagePage.getContent();
    }

    /***
     * 修改库存页面查询
     * @param storage
     * @param dateInProducedd
     * @return
     */
    @Override
    public Map<String, Object> selectEditt(Storage storage, String dateInProducedd, Integer page, Integer rows) {
        Map<String, Object> map = new HashMap<>();
        String selectSqlStart = "select a.id, " +
                "clientname, " +
                "a.name, " +
                "length, " +
                "model, " +
                "price, " +
                "realityweight, " +
                "sale_number as saleNumber, " +
                "DATE_FORMAT(date_in_produced,'%Y-%m-%d %H:%i:%s') as dateInProduced, " +
                "clerk_name as clerk, " +
                "group_name as groupName, " +
                "b.name as location, " +
                "ji_tai_name as jitai, " +
                "state from t_storage a left join t_location b on a.location_id = b.id where true ";

        String sql = "";
        String pg = "";
        if (page != null && rows != null) {
            pg += " LIMIT " + (page - 1) * rows + ", " + rows;
        }
        Pageable pageable = new PageRequest(page - 1, rows);
        if (StringUtil.isNotEmpty(storage.getSaleNumber())) {
            sql += " and sale_number = like '%" + storage.getSaleNumber() + "%'";
        }
        if (StringUtil.isNotEmpty(storage.getName())) {
            sql += " and a.name = '" + storage.getName() + "'";
        }
        if (storage.getLocation() != null) {
            sql += " and a.location_id = " + storage.getLocation().getId();
        }
        if (storage.getJiTai() != null) {
            sql += " and a.ji_tai_id = " + storage.getJiTai().getId();
        }
        if (StringUtil.isNotEmpty(storage.getPeasant())) {
            sql += " and a.peasant = '" + storage.getPeasant() + "'";
        }
        if (StringUtil.isNotEmpty(dateInProducedd) && storage.getGroup() != null) {
            if (StringUtil.isNotEmpty(dateInProducedd) && !storage.getGroupName().equals("夜班")) {
                System.out.println("白班");
                String star = dateInProducedd + " 00:00:00";
                String end = dateInProducedd + " 23:59:59";
                sql += " and a.date_in_produced BETWEEN '" + star + "' and '" + end + "'";
            } else {
                System.out.println("夜班");
                String starr = dateInProducedd + " 17:00:00";
                String endd = dateInProducedd.split("-")[0] + "-" + dateInProducedd.split("-")[1] + "-" + (Integer.parseInt(dateInProducedd.split("-")[2]) + 1) + " 14:00:00";
                sql += " and a.date_in_produced BETWEEN '" + starr + "' and '" + endd + "'";
            }
        }

        if (StringUtil.isNotEmpty(dateInProducedd) && storage.getGroup() == null) {
            String star = dateInProducedd + " 00:00:00";
            String end = dateInProducedd + " 23:59:59";
            sql += " and a.date_in_produced BETWEEN '" + star + "' and '" + end + "'";
        }
        if (storage.getGroup() != null) {
            sql += " and a.group_name = '" + storage.getGroup().getName() + "'";
        }
        if (storage.getClerk() != null) {
            sql += " and a.clerk_id = " + storage.getId();
        }
        if (StringUtil.isNotEmpty(storage.getClientname())) {
            sql += " and a.clientname = '" + clientService.findById(Integer.parseInt(storage.getClientname())).getName() + "'";
        }
        if (storage.getLength() != null) {
            sql += " and a.length = " + storage.getLength();
        }
        if (storage.getModel() != null) {
            sql += " and a.model = " + storage.getModel();
        }
        if (StringUtil.isNotEmpty(storage.getPrice())) {
            sql += " and a.price = '" + storage.getPrice() + "'";
        }
        if (storage.getRealityweight() != null) {
            sql += " and a.realityweight = " + storage.getRealityweight();
        }
        if (storage.getId() != null) {
            sql += " and a.id = " + storage.getId();
        }
        if (StringUtil.isNotEmpty(storage.getState())) {
            String state = storage.getState();
            System.out.println(storage.getState());
            if (storage.getState().startsWith("'")) {
                state = storage.getState().substring(1, storage.getState().length() - 1);
                System.out.println(state);
            }
            sql += " and a.state like '%" + storage.getState() + "%'";
        }
        if (StringUtil.isNotEmpty(storage.getColor())) {
            sql += " and a.color = '" + storage.getColor() + "'";
        }

        LogUtil.printLog("===修改库存查询===");
        LogUtil.printLog("查询所有信息语句：" + selectSqlStart + sql + pg);
        List result = GetResultUtils.getResult(selectSqlStart + sql + pg, entityManager);

        LogUtil.printLog("查询总条数语句：" + selectSqlStart + sql + pg);
        Integer count = GetResultUtils.getInteger("select count(id) from t_storage a where true" + sql, entityManager);

        map.put("rows", result);
        map.put("total", count);
        map.put("success", true);
        return map;
    }

    @Override
    public List<Storage> findBySaleListProductId(int id) {
        return storageRepository.findBySaleListProductId(id);
    }

    @Override
    public void updateshijian(Integer id, Date parse) {
        storageRepository.updateshijian(id, parse);
    }

    @Override
    public Integer countBySaleListProductIdDetail(Integer id, Storage storage, String state, String date) {
        Long count = storageRepository.count(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicates = cb.conjunction();
                if (id != null && id != 0) {
                    predicates.getExpressions().add(cb.equal(root.get("saleListProduct").get("id"), id));
                    predicates.getExpressions().add(cb.equal(root.get("name"), storage.getName()));
                    predicates.getExpressions().add(cb.equal(root.get("model"), storage.getModel()));
                    predicates.getExpressions().add(cb.equal(root.get("price"), storage.getPrice()));
                    predicates.getExpressions().add(cb.equal(root.get("length"), storage.getLength()));
                    predicates.getExpressions().add(cb.equal(root.get("color"), storage.getColor()));
                    predicates.getExpressions().add(cb.equal(root.get("realityweight"), storage.getRealityweight()));
                    predicates.getExpressions().add(cb.equal(root.get("dao"), storage.getDao()));
                    predicates.getExpressions().add(cb.equal(root.get("peasant"), storage.getPeasant()));
                    predicates.getExpressions().add(cb.equal(root.get("clientname"), storage.getClientname()));
                    predicates.getExpressions().add(cb.like(root.get("state"), state));
                    predicates.getExpressions().add(cb.equal(root.get("outNumber"), storage.getOutNumber()));
                    if (StringUtil.isNotEmpty(date)) {
                        try {
                            String st = date + " 00:00:00";
                            String ed = date + " 23:59:59";
                            System.out.println(st);
                            System.out.println(ed);
                            Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(st);
                            Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ed);
                            System.out.println(star);
                            System.out.println(end);
                            predicates.getExpressions().add(cb.greaterThanOrEqualTo(root.get("deliveryTime"), star));
                            predicates.getExpressions().add(cb.lessThanOrEqualTo(root.get("deliveryTime"), end));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                query.groupBy(root.get("saleListProduct").get("id"), root.get("name"), root.get("model"), root.get("price"), root.get("length"), root.get("color"), root.get("realityweight"), root.get("dao"), root.get("peasant"), root.get("clientname"), root.get("outNumber"), root.get("dabaonum"));
                return predicates;
            }
        });
        return count.intValue();
    }

    @Override
    public Long getCount(Storage storage, String dateInProducedd) {
        Long count = storageRepository.count(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtil.isNotEmpty(storage.getSaleNumber())) {
                    predicate.getExpressions().add(cb.like(root.get("saleNumber"), "%" + storage.getSaleNumber() + "%"));
                }
                if (StringUtil.isNotEmpty(storage.getName())) {
                    predicate.getExpressions().add(cb.equal(root.get("name"), storage.getName()));
                }
                if (storage.getLocation() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("location").get("id"), storage.getLocation().getId()));
                }
                if (storage.getJiTai() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("jiTai").get("id"), storage.getJiTai().getId()));
                }
                if (StringUtil.isNotEmpty(storage.getPeasant())) {
                    predicate.getExpressions().add(cb.equal(root.get("peasant"), storage.getPeasant()));
                }
                if (StringUtil.isNotEmpty(dateInProducedd) && storage.getGroup() != null) {
                    if (StringUtil.isNotEmpty(dateInProducedd) && !storage.getGroupName().equals("夜班")) {
                        System.out.println("白班");
                        try {
                            java.util.Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 00:00:00");
                            java.util.Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 23:59:59");
                            System.out.println("开始时间：" + star);
                            System.out.println("结束时间：" + end);
                            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                            predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("夜班");
                        String starr = dateInProducedd + " 17:00:00";
                        String endd = dateInProducedd.split("-")[0] + "-" + dateInProducedd.split("-")[1] + "-" + (Integer.parseInt(dateInProducedd.split("-")[2]) + 1) + " 14:00:00";
                        try {
                            Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(starr);
                            Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endd);
                            System.out.println("开始时间：" + star);
                            System.out.println("结束时间：" + end);
                            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                            predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    predicate.getExpressions().add(cb.equal(root.get("group"), storage.getGroup()));
                }

                if (StringUtil.isNotEmpty(dateInProducedd) && storage.getGroup() == null) {
                    try {
                        java.util.Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 00:00:00");
                        java.util.Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd + " 23:59:59");
                        System.out.println("开始时间：" + star);
                        System.out.println("结束时间：" + end);
                        predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                        predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (storage.getClerk() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("clerk").get("id"), storage.getClerk().getId()));
                }
                if (StringUtil.isNotEmpty(storage.getClientname())) {
                    predicate.getExpressions().add(cb.equal(root.get("clientname"), clientService.findById(Integer.parseInt(storage.getClientname())).getName()));
                }
                if (storage.getLength() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("length"), storage.getLength()));
                }
                if (storage.getModel() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("model"), storage.getModel()));
                }
                if (StringUtil.isNotEmpty(storage.getPrice())) {
                    predicate.getExpressions().add(cb.equal(root.get("price"), storage.getPrice()));
                }
                if (storage.getRealityweight() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("realityweight"), storage.getRealityweight()));
                }
                if (StringUtil.isNotEmpty(storage.getState())) {
                    String state = storage.getState();
                    System.out.println(storage.getState());
                    if (storage.getState().startsWith("'")) {
                        state = storage.getState().substring(1, storage.getState().length() - 1);
                        System.out.println(state);
                    }
                    predicate.getExpressions().add(cb.like(root.get("state"), "%" + state + "%"));
                }
                if (StringUtil.isNotEmpty(storage.getColor())) {
                    predicate.getExpressions().add(cb.equal(root.get("color"), storage.getColor()));
                }
                return predicate;
            }
        });
        return count;
    }

    @Override
    public Integer kucunCount(Storage storage, String dateInProducedd, String dateInProduceddd) {
        System.out.println(storage);
        Long count = storageRepository.count(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicates = cb.conjunction();
                if (storage.getSaleListProduct() != null && storage.getSaleListProduct().getId() != null) {
                    predicates.getExpressions().add(cb.equal(root.get("saleListProduct").get("id"), storage.getSaleListProduct().getId()));
                }
                predicates.getExpressions().add(cb.equal(root.get("name"), storage.getName()));
                predicates.getExpressions().add(cb.equal(root.get("model"), storage.getModel()));
                predicates.getExpressions().add(cb.equal(root.get("price"), storage.getPrice()));
                predicates.getExpressions().add(cb.equal(root.get("length"), storage.getLength()));
                predicates.getExpressions().add(cb.equal(root.get("color"), storage.getColor()));
                predicates.getExpressions().add(cb.equal(root.get("realityweight"), storage.getRealityweight()));
//                if (storage.getDao() != null) {
//                    predicates.getExpressions().add(cb.equal(root.get("dao"), storage.getDao()));
//                } else {
//                    predicates.getExpressions().add(cb.isNull(root.get("dao")));
//                }
                predicates.getExpressions().add(cb.equal(root.get("peasant"), storage.getPeasant()));
                predicates.getExpressions().add(cb.equal(root.get("dabaonum"), storage.getDabaonum()));
                predicates.getExpressions().add(cb.equal(root.get("clientname"), storage.getClientname()));
                if (storage.getUnitPrice() != null) {
                    predicates.getExpressions().add(cb.equal(root.get("unitPrice"), storage.getUnitPrice()));
                } else {
                    predicates.getExpressions().add(cb.isNull(root.get("unitPrice")));
                }
                predicates.getExpressions().add(cb.like(root.get("state"), "%生产完成%"));
//                query.groupBy(root.get("saleListProduct").get("id"), root.get("name"), root.get("model"), root.get("price"), root.get("length"), root.get("color"), root.get("realityweight"), root.get("dao"), root.get("peasant"), root.get("clientname"));
                return predicates;
            }
        });
        return count.intValue();
    }

    @Override
    public Integer countByDetail(Storage storage, String date) {
        Long count = storageRepository.count(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicates = cb.conjunction();
                if (storage.getSaleListProduct() != null) {
                    predicates.getExpressions().add(cb.equal(root.get("saleListProduct").get("id"), storage.getSaleListProduct().getId()));
                }
                predicates.getExpressions().add(cb.equal(root.get("name"), storage.getName()));
                predicates.getExpressions().add(cb.equal(root.get("model"), storage.getModel()));
                predicates.getExpressions().add(cb.equal(root.get("price"), storage.getPrice()));
                predicates.getExpressions().add(cb.equal(root.get("length"), storage.getLength()));
                predicates.getExpressions().add(cb.equal(root.get("color"), storage.getColor()));
                predicates.getExpressions().add(cb.equal(root.get("realityweight"), storage.getRealityweight()));
                if (storage.getDao() != null) {
                    predicates.getExpressions().add(cb.equal(root.get("dao"), storage.getDao()));
                } else {
                    predicates.getExpressions().add(cb.isNull(root.get("dao")));
                }
                predicates.getExpressions().add(cb.equal(root.get("peasant"), storage.getPeasant()));
                predicates.getExpressions().add(cb.equal(root.get("clientname"), storage.getClientname()));
                predicates.getExpressions().add(cb.equal(root.get("dabaonum"), storage.getDabaonum()));
                predicates.getExpressions().add(cb.like(root.get("state"), "%装车%"));
                predicates.getExpressions().add(cb.equal(root.get("outNumber"), storage.getOutNumber()));
                return predicates;
            }
        });
        return count.intValue();
    }

    @Override
    public List<Storage> findLingShou(Storage storage) {
        return storageRepository.findAll(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (storage.getId() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("id"), storage.getId()));
                }
                if (StringUtil.isNotEmpty(storage.getClientname())) {
                    predicate.getExpressions().add(cb.equal(root.get("clientname"), storage.getClientname()));
                }
                if (storage.getModel() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("model"), storage.getModel()));
                }
                if (StringUtil.isNotEmpty(storage.getPrice())) {
                    predicate.getExpressions().add(cb.equal(root.get("price"), storage.getPrice()));
                }
                if (storage.getLength() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("length"), storage.getLength()));
                }
                predicate.getExpressions().add(cb.like(root.get("state"), "%生产完成%"));
                return predicate;
            }
        });
    }

    @Override
    public void save(List<Storage> storages) {
        storageRepository.save(storages);
    }

    @Override
    public void updateState(String state, Integer key) {
        storageRepository.updateState(state, key);
    }

    @Override
    public boolean findBySaleListId(Integer id) {
        List<Storage> storages = storageRepository.findBySaleListId(id);
        if (storages.size() != 0) {
            return false;
        }

        return true;
    }

    @Override
    public List<Storage> findBySaleListProductIds(String... ids) {
        return storageRepository.findBySaleListIds(ids);
    }

    @Test
    public void test() {
        String SelectSqlStar = "select " +
                "clientname, " +
                "peasant, " +
                "sale_number, " +
                "name, " +
                "model, " +
                "price, " +
                "length, " +
                "color, " +
                "count(id) as sum, " +
                "realityweight as weight, " +
                "FORMAT(realityweight * count(id), 2) as sumWeight, " +
                "dao, " +
                "demand, " +
                "unit_price, " +
                "FORMAT(unit_price * count(id), 2) as total_price " +
                "from t_storage where state like '%生产完成%'";
        String SelectSqlEnd = "group by " +
                "sale_list_product_id," +
                " name, " +
                "model, " +
                "price, " +
                "length , " +
                "color , " +
                "realityweight , " +
                "peasant , " +
                "clientname , " +
                "dabaonum , " +
                "unit_price " +
                "order by peasant asc, " +
                "name asc, " +
                "model asc, " +
                "price asc, " +
                "length asc, " +
                "color asc, " +
                "realityweight asc";
        System.out.println(SelectSqlStar + SelectSqlEnd);
    }

    @Override
    public Map<String, Object> selecttt(Storage storage, String dateInProducedd, String dateInProduceddd, Integer page, Integer rows) {
        Map<String, Object> map = new HashMap<>();
        String selectSqlStar = "select " +
                "code," +
                "clientname, " +
                "peasant, " +
                "sale_number, " +
                "name, " +
                "model, " +
                "length, " +
                "price, " +
                "realityweight as weight, " +
                "color, " +
                "count(id) as sum, " +
                "ROUND(realityweight * count(id), 2) as sumweight, " +
                "dao, " +
                "unit_price, " +
                "ROUND(unit_price * count(id), 2) as total_price, " +
                "demand " +
                "from t_storage where state like '%生产完成%'";

       /* String selectSqlStar = "select " +
                "b.code, " +
                "a.clientname, " +
                "a.peasant, " +
                "a.sale_number, " +
                "a.name, " +
                "a.model, " +
                "a.length, " +
                "a.price, " +
                "a.realityweight as weight, " +
                "a.color, " +
                "count(a.id) as sum, " +
                "ROUND(a.realityweight * count(a.id), 2) as sumweight, " +
                "a.dao, " +
                "a.unit_price, " +
                "ROUND(a.unit_price * count(a.id), 2) as total_price, " +
                "a.demand " +
                "from t_storage a, t_sale_list_product b " +
                "where a.state like '%生产完成%' " +
                "and a.sale_list_product_id = b.id";*/

        String selectSqlEnd = "group by " +
                "sale_list_product_id," +
                "name, " +
                "model, " +
                "price, " +
                "length , " +
                "color , " +
                "realityweight , " +
                "peasant , " +
                "clientname , " +
                "dabaonum , " +
                "unit_price " +
                "order by peasant asc, " +
                "name asc, " +
                "model asc, " +
                "price asc, " +
                "length asc, " +
                "color asc, " +
                "realityweight asc";

        String pg = "";
        if (page != null && rows != null) {
            Integer star = (page - 1) * rows;
            pg += " limit " + star + "," + rows + "";
        }

        //查询总数量
        String countSqlStart = "select count(id) as b from t_storage where state like '%生产完成%'";

        //查询总重量
        String sumWeightSqlStar = "select ROUND(sum(realityweight),2) from t_storage where state like '%生产完成%'";

        String sql = "";

        if (StringUtil.isNotEmpty(storage.getSaleNumber())) {
            sql += " and sale_number like '%" + storage.getSaleNumber() + "%'";
        }
        if (StringUtil.isNotEmpty(storage.getName())) {
            sql += " and name = '" + storage.getName() + "'";
        }
        if (storage.getLocation() != null) {
            sql += " and location = '" + storage.getLocation() + "'";
        }
        if (storage.getJiTai() != null) {
            sql += " and ji_tai_id = " + storage.getJiTai().getId();
        }
        if (StringUtil.isNotEmpty(storage.getPeasant())) {
            sql += " and peasant = '" + storage.getPeasant() + "'";
        }
        if (StringUtil.isNotEmpty(dateInProducedd) && StringUtil.isNotEmpty(dateInProduceddd)) {
            sql += " and date_in_produced BETWEEN '" + dateInProducedd + "'" + "and '" + dateInProduceddd + "'";
//            try {
////                Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd);
////                Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProduceddd);
////                System.out.println("开始时间：" + star);
////                System.out.println("结束时间：" + end);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
        }
        if (storage.getClerk() != null) {
            sql += " and clerk_id = " + storage.getClerk().getId();
        }
        if (StringUtil.isNotEmpty(storage.getClientname())) {
            sql += " and clientname = '" + clientService.findById(Integer.parseInt(storage.getClientname())).getName() + "'";
        }
        if (storage.getLength() != null) {
            sql += " and length = " + storage.getLength();
        }
        if (storage.getModel() != null) {
            sql += " and model = " + storage.getModel();
        }
        if (StringUtil.isNotEmpty(storage.getPrice())) {
            sql += " and price = " + storage.getPrice();
        }
        if (storage.getRealityweight() != null) {
            sql += " and realityweight = " + storage.getRealityweight();
        }
        if (StringUtil.isNotEmpty(storage.getColor())) {
            sql += " and color = '" + storage.getColor() + "'";
        }

        System.out.println("查询所有的sql语句：");
        System.out.println(selectSqlStar + sql + selectSqlEnd);

        List result = GetResultUtils.getResult(selectSqlStar + sql + selectSqlEnd + pg, entityManager);


        //执行计算总件数的sql
        System.out.println("查询总件数的sql：");
        System.out.println(countSqlStart + sql);
        Integer count = GetResultUtils.getInteger(countSqlStart + sql, entityManager);
        System.out.println("总件数：" + count);

        //执行查询总重量的sql
        System.out.println("查询总重量sql：");
        System.out.println(sumWeightSqlStar + sql);
        Double totalWeight = GetResultUtils.getDouble(sumWeightSqlStar + sql, entityManager);
        System.out.println("总重量：" + totalWeight);

        map.put("success", true);
        map.put("rows", result);
        map.put("count", count);
        map.put("totalWeight", totalWeight);
        map.put("total", GetResultUtils.getResult(selectSqlStar + sql + selectSqlEnd, entityManager).size());
        return map;
    }

    @Override
    public Long getKuCunCount(Storage storage, String dateInProducedd, String dateInProduceddd) {
        Long count = storageRepository.count(new Specification<Storage>() {
            @Override
            public Predicate toPredicate(Root<Storage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtil.isNotEmpty(storage.getSaleNumber())) {
                    predicate.getExpressions().add(cb.like(root.get("saleNumber"), "%" + storage.getSaleNumber() + "%"));
                }
                if (StringUtil.isNotEmpty(storage.getName())) {
                    predicate.getExpressions().add(cb.equal(root.get("name"), storage.getName()));
                }
                if (storage.getLocation() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("location").get("id"), storage.getLocation().getId()));
                }
                if (storage.getJiTai() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("jiTai").get("id"), storage.getJiTai().getId()));
                }
                if (StringUtil.isNotEmpty(storage.getPeasant())) {
                    predicate.getExpressions().add(cb.equal(root.get("peasant"), storage.getPeasant()));
                }
                if (StringUtil.isNotEmpty(dateInProducedd) && StringUtil.isNotEmpty(dateInProduceddd)) {
                    try {
                        Date star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProducedd);
                        Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateInProduceddd);
                        System.out.println("开始时间：" + star);
                        predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dateInProduced"), star));
                        System.out.println("结束时间：" + end);
                        predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("dateInProduced"), end));
                        query.groupBy(root.get("dateInProduced"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (storage.getClerk() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("clerk").get("id"), storage.getClerk().getId()));
                }
                if (StringUtil.isNotEmpty(storage.getClientname())) {
                    predicate.getExpressions().add(cb.equal(root.get("clientname"), clientService.findById(Integer.parseInt(storage.getClientname())).getName()));
                }
                if (storage.getLength() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("length"), storage.getLength()));
                }
                if (storage.getModel() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("model"), storage.getModel()));
                }
                if (StringUtil.isNotEmpty(storage.getPrice())) {
                    predicate.getExpressions().add(cb.equal(root.get("price"), storage.getPrice()));
                }
                if (storage.getRealityweight() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("realityweight"), storage.getRealityweight()));
                }
                if (StringUtil.isNotEmpty(storage.getState())) {
                    String state = storage.getState();
                    System.out.println(storage.getState());
                    if (storage.getState().startsWith("'")) {
                        state = storage.getState().substring(1, storage.getState().length() - 1);
                        System.out.println(state);
                    }
                    predicate.getExpressions().add(cb.like(root.get("state"), "%" + state + "%"));
                }
                if (StringUtil.isNotEmpty(storage.getColor())) {
                    predicate.getExpressions().add(cb.equal(root.get("color"), storage.getColor()));
                }
                predicate.getExpressions().add(cb.like(root.get("state"), "%生产完成%"));
                query.groupBy(root.get("saleListProduct").get("id"), root.get("name"), root.get("model"), root.get("price"), root.get("length"), root.get("color"), root.get("realityweight"), root.get("dao"), root.get("peasant"), root.get("clientname"), root.get("dabaonum"), root.get("unitPrice"));
                return predicate;
            }
        });
        return count;
    }

    @Override
    public Map<String, Object> detaill(Map<String, Object> map) {
        Map<String, Object> data = new HashMap<>();


        String selectDataSqlStart = "select clientname, peasant, " +
                "out_number as outnumber, " +
                "sale_number as salenumber, " +
                "name, model,code, " +
                "length, price, " +
                "count(id) as num, " +
                "realityweight, " +
                "ROUND(count(id) * realityweight, 2) as numweight, " +
                "unit_price as unitprice, " +
                "ROUND(count(id) * unit_price) as totalprice, " +
                "color " +
                "from t_storage " +
                "where state like '%装车%'";

        String selectDataSqlEnd = "group by sale_list_product_id, " +
                "name, " +
                "model, " +
                "price, " +
                "length, " +
                "color, " +
                "realityweight, " +
                "dao, " +
                "peasant, " +
                "clientname, " +
                "out_number, " +
                "dabaonum " +
                "order by name asc, " +
                "peasant asc";

        String selectCountSqlStart = "select count(*) from t_storage where state LIKE '%装车%'";

        String selectSumWeightStart = "select ROUND(sum(realityweight), 2) from t_storage where state LIKE '%装车%'";

        String sql = "";
        if (StringUtil.isNotEmpty((String) map.get("client"))) {
            sql += " and clientname = '" + map.get("client") + "'";
        }
        if (StringUtil.isNotEmpty((String) map.get("peasant"))) {
            sql += " and peasant = '" + map.get("peasant") + "'";
        }
        if (StringUtil.isNotEmpty((String) map.get("product"))) {
            sql += " and name = '" + map.get("product") + "'";
        }
        if (StringUtil.isNotEmpty((String) map.get("chukudanhao"))) {
            sql += " and out_number = '" + map.get("chukudanhao") + "'";
        }

        if (StringUtil.isNotEmpty((String) map.get("date"))) {
            String date = (String) map.get("date");
            String st = date + " 00:00:00";
            String ed = date + " 23:59:59";
            sql += " and delivery_time BETWEEN '" + st + "' and '" + ed + "'";
        }

        //获取所有结果
        System.out.println("查询的sql");
        System.out.println(selectDataSqlStart + sql + selectDataSqlEnd);
        List result = GetResultUtils.getResult(selectDataSqlStart + sql + selectDataSqlEnd, entityManager);

        //获取总数量
        System.out.println("查询数量sql：");
        System.out.println(selectCountSqlStart + sql);
        Integer count = GetResultUtils.getInteger(selectCountSqlStart + sql, entityManager);

        //获取总重量
        System.out.println("查询总重量sql：");
        System.out.println(selectSumWeightStart + sql);
        Double sumweight = GetResultUtils.getDouble(selectSumWeightStart + sql, entityManager);

        data.put("success", true);
        data.put("rows", result);
        data.put("count", count);
        data.put("sumweight", sumweight);
        return data;
    }

    /***
     * 根据开始时间结束时间和客户名统计
     * @param stardate
     * @param enddate
     * @param clientname
     * @return
     */
    @Override
    public List<Map<String, Object>> tongji(String stardate, String enddate, String clientname) throws ParseException {
        LogUtil.printLog("<---统计查询开始");

        //查询所有的订单号
        String[] saleNumbers = saleListRepository.findSaleNumbers(DateUtil.getDate(stardate), DateUtil.getDate(enddate), clientService.findName(clientname).getId());
        //查询所有的订单id
        Integer[] saleListIds = saleListRepository.findsaleListIds(DateUtil.getDate(stardate), DateUtil.getDate(enddate), clientService.findName(clientname).getId());

        LogUtil.printLog("订单开始日期：" + stardate);
        LogUtil.printLog("订单结束日期：" + enddate);
        LogUtil.printLog("客户名：" + clientname);
        LogUtil.printLog("查询到的订单数量：" + saleNumbers.length);
        StringBuilder sb = new StringBuilder();
        for (String saleNumber : saleNumbers) {
            sb.append(saleNumber + ",");
        }
        LogUtil.printLog("查询到的订单号：" + sb.toString());

        //查询下单理论重量
        String sql = "select IFNULL(sum(sumwight), 0) from t_sale_list_product a left join t_sale_list b " +
                "on a.sale_list_id = b.id and a.clientname = '" + clientname + "' where b.sale_date BETWEEN '" + stardate + "' AND '" + enddate + "'";
        Double lilunzhong = GetResultUtils.getDouble(sql, entityManager);
        LogUtil.printLog("查询下单理论重量:" + sql);
        LogUtil.printLog("下单理论重量:" + lilunzhong);

        //实际生产重量
        Double ysc = storageRepository.findYSCZL(saleNumbers);
        LogUtil.printLog("已经生产的重量：" + ysc);

        //未生产的重量
        Double wsc = saleListProductRepository.findWSC(saleListIds);
        if (wsc < 0) {
            wsc = 0.0;
        }
        LogUtil.printLog("未生产的重量：" + wsc);
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("xiadanzhongliang", lilunzhong);
        map.put("weishengchan", wsc);
        map.put("yiwancheng", ysc);
        list.add(map);
        LogUtil.printLog("统计查询结束--->");
        return list;
    }

    @Override
    public List<Map<String, Object>> tongji(String stardate, String enddate) throws ParseException {
        LogUtil.printLog("<---统计查询开始");

        //查询所有的订单号
        String[] saleNumbers = saleListRepository.findSaleNumbers(DateUtil.getDate(stardate), DateUtil.getDate(enddate));
        //查询所有的订单id
        Integer[] saleListIds = saleListRepository.findsaleListIds(DateUtil.getDate(stardate), DateUtil.getDate(enddate));

        LogUtil.printLog("订单开始日期：" + stardate);
        LogUtil.printLog("订单结束日期：" + enddate);
        LogUtil.printLog("查询到的订单数量：" + saleNumbers.length);
        StringBuilder sb = new StringBuilder();
        for (String saleNumber : saleNumbers) {
            sb.append(saleNumber + ",");
        }
        LogUtil.printLog("查询到的订单号：" + sb.toString());

        //查询下单理论重量
        String sql = "select IFNULL(sum(sumwight), 0) from t_sale_list_product a left join t_sale_list b " +
                "on a.sale_list_id = b.id where b.sale_date BETWEEN '" + stardate + "' AND '" + enddate + "'";
        Double lilunzhong = GetResultUtils.getDouble(sql, entityManager);
        LogUtil.printLog("查询下单理论重量:" + sql);
        LogUtil.printLog("下单理论重量:" + lilunzhong);

        //实际生产重量
        Double ysc = storageRepository.findYSCZL(saleNumbers);
        LogUtil.printLog("已经生产的重量：" + ysc);

        //未生产的重量
        Double wsc = saleListProductRepository.findWSC(saleListIds);
        if (wsc < 0) {
            wsc = 0.0;
        }
        LogUtil.printLog("未生产的重量：" + wsc);
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("xiadanzhongliang", lilunzhong);
        map.put("weishengchan", wsc);
        map.put("yiwancheng", ysc);
        list.add(map);
        LogUtil.printLog("统计查询结束--->");
        return list;
    }

    @Override
    public void updatePanDianJiByIds(String pandianji, Integer... ids) {
        storageRepository.updatePanDianJiByIds(pandianji, ids);
    }

}
