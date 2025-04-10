package com.open.ai.eros.common.constants;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.vo.MenuMetaVo;
import com.open.ai.eros.common.vo.MenuVo;
import io.github.classgraph.json.JSONUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum MenuEnum {
    creator_dashboard("app","creatorDashboard","menu.creatorDashboard",RoleEnum.CREATOR.getRole(),1,"vuestic-iconset-dashboard",null),


    user_manager("app","users","menu.users", RoleEnum.SYSTEM.getRole(),1,"manage_accounts",null),
    permission_manager("app","permissions","menu.permission",RoleEnum.SYSTEM.getRole(),1,"settings_accessibility","[{\"name\":\"rolePermissions\",\"displayName\":\"menu.rolePermission\"},{\"name\":\"permissions\",\"displayName\":\"menu.permission\"}]"),
    announcement_manager("app","announcement","menu.announcement",RoleEnum.SYSTEM.getRole(),1,"campaign",null),
    mask_manager("app","mask","menu.mask",RoleEnum.CREATOR.getRole(),1,"masks","[{\"name\":\"mask\",\"displayName\":\"menu.mask\"}]"),
    model_manager("app","model","menu.modelConfig",RoleEnum.SYSTEM.getRole(),1,"psychology","[{\"name\":\"modelConfig\",\"displayName\":\"menu.modelConfig\"}]"),
    reply_template_manager("app","replyTemplate","menu.replyTemplate",RoleEnum.CREATOR.getRole(),1,"density_medium","[{\"name\":\"replyTemplate\",\"displayName\":\"menu.replyTemplate\"},{\"name\":\"wordChannel\",\"displayName\":\"menu.wordChannel\"}]"),
    system_dashboard("app","dashboard","menu.dashboard",RoleEnum.SYSTEM.getRole(),1,"vuestic-iconset-dashboard",null),

    payment_manager("app","payments","menu.payments",RoleEnum.SYSTEM.getRole(),1,"shopping_cart","[{\"name\":\"goodManager\",\"displayName\":\"menu.goodManager\"},\n" +
            " {\"name\":\"rightManager\",\"displayName\":\"menu.rightManager\"},{\"name\":\"orderManager\",\"displayName\":\"menu.orderManager\"},{\"name\":\"exchangeCode\",\"displayName\":\"menu.exchangeCode\"}]"),
    konwledge_manager("app","knowledge","menu.knowledge",RoleEnum.CREATOR.getRole(),1,"school","[{\"name\":\"knowledge\",\"displayName\":\"menu.knowledgeManager\"}]");

    @Getter
    private String group;// 分组
    private String name;
    private String  menuName;
    private String role;
    private Integer order;
    private String icon;
    private String children;




    MenuEnum(String group, String name, String menuName, String role, Integer order,String icon,String children) {
        this.group = group;
        this.name = name;
        this.menuName = menuName;
        this.role = role;
        this.order = order;
        this.icon = icon;
        this.children = children;
    }

    public static List<MenuVo> getRoleMenu(String role){
        boolean isAdmin = RoleEnum.SYSTEM.getRole().equals(role);
        List<MenuVo> menuVos = new ArrayList<>();
        if(StringUtils.isBlank(role)){
            return menuVos;
        }
        for (MenuEnum value : MenuEnum.values()) {
            if( value.role.equals(role) ||  isAdmin ){
                menuVos.add(
                        MenuVo.builder()
                                .name(value.getName())
                                .displayName(value.getMenuName())
                                .meta(MenuMetaVo.builder().icon(value.getIcon()).build())
                                .children(value.children != null ? JSONObject.parseArray(value.children,MenuVo.class):null)
                                .build()
                );
            }
        }
        return menuVos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
