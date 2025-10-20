package work.soho.shop.biz.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.shop.biz.domain.ShopRegion;
import work.soho.test.TestApp;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
@Log4j2
class ShopRegionServiceTest {
    @Autowired
    private ShopRegionService shopRegionService;

    private String readData() {
        String path = "/media/fang/ssd1t/home/fang/work/tmp/regionData.json";
        return FileUtil.readLines(path, "utf-8").toString();
    }

    /**
     * 递归遍历地区数据
     * @param districts 地区数组
     * @param level 当前层级（用于缩进显示）
     */
    private void traverseDistricts(JSONArray districts, int level, String parentAdCode) {
        System.out.println("┌─ 层级 " + level + " ──────────────────────");
//        System.out.println(districts);
//        System.out.println(districts.isEmpty());
        if (districts == null || districts.isEmpty()) {
            System.out.println("└─────────────────────────────");
            return;
        }

        int sortOrder = 0;
        for (Object item : districts) {
            if (item instanceof JSONObject) {
                sortOrder++;
                JSONObject district = (JSONObject) item;

                ShopRegion shopRegion = new ShopRegion();
                shopRegion.setRegionName(district.getStr("name"));
                shopRegion.setRegionCode(district.getStr("adcode"));
                shopRegion.setRegionLevel(level);
                shopRegion.setParentCode(parentAdCode);
                shopRegion.setIsRemote(0);
                shopRegion.setSortOrder(sortOrder);
                try {
                    shopRegionService.save(shopRegion);
                } catch (DuplicateKeyException e) {
                    shopRegion.setRegionCode(shopRegion.getRegionCode() + "-" + sortOrder);
                    shopRegionService.save(shopRegion);
                }


                // 输出字段信息
                String indent = "  ".repeat(level); // 根据层级缩进
                System.out.println(indent + "┌─ 地区信息 ──────────────────────");
                System.out.println(indent + "│ 名称(name): " + district.getStr("name"));
                System.out.println(indent + "│ 编码(adcode): " + district.getStr("adcode"));
                System.out.println(indent + "│ 中心点(center): " + district.getStr("center"));
                System.out.println(indent + "│ 层级(level): " + district.getStr("level"));


                // 递归处理子地区
                JSONArray subDistricts = district.getJSONArray("districts");
                if (subDistricts != null && !subDistricts.isEmpty()) {
                    System.out.println(indent + "│ 子地区数量: " + subDistricts.size());
                    System.out.println(indent + "└─ 子地区列表 ───────────────────");
                    traverseDistricts(subDistricts, level + 1, shopRegion.getRegionCode());
                } else {
                    System.out.println(indent + "└─────────────────────────────");
                }
            } else if (item instanceof JSONArray) {
                JSONArray subDistricts = (JSONArray) item;
//                System.out.println(indent + "└─ 子地区列表 ───────────────────");
                traverseDistricts(subDistricts, level + 1, "0");
            }
        }
    }

    @Test
    public void syncData() {
        System.out.println("syncData");
        JSONArray jsonArray = JSONUtil.parseArray(readData());
        traverseDistricts(jsonArray, 0, "0");
    }


//    public static class Region {
//        private String name;
//        private String adcode;
//        private String level;
//    }
}