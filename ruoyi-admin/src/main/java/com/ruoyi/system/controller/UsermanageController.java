package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.Usermanage;
import com.ruoyi.system.service.IUsermanageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户信息Controller
 * 
 * @author ruoyi
 * @date 2025-10-22
 */
@RestController
@RequestMapping("/system/usermanage")
public class UsermanageController extends BaseController
{
    @Autowired
    private IUsermanageService usermanageService;

    /**
     * 查询用户信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:usermanage:list')")
    @GetMapping("/list")
    public TableDataInfo list(Usermanage usermanage)
    {
        startPage();
        List<Usermanage> list = usermanageService.selectUsermanageList(usermanage);
        return getDataTable(list);
    }

    /**
     * 导出用户信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:usermanage:export')")
    @Log(title = "用户信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Usermanage usermanage)
    {
        List<Usermanage> list = usermanageService.selectUsermanageList(usermanage);
        ExcelUtil<Usermanage> util = new ExcelUtil<Usermanage>(Usermanage.class);
        util.exportExcel(response, list, "用户信息数据");
    }

    /**
     * 获取用户信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:usermanage:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        return success(usermanageService.selectUsermanageByUserId(userId));
    }

    /**
     * 新增用户信息
     */
    @PreAuthorize("@ss.hasPermi('system:usermanage:add')")
    @Log(title = "用户信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Usermanage usermanage)
    {
        return toAjax(usermanageService.insertUsermanage(usermanage));
    }

    /**
     * 修改用户信息
     */
    @PreAuthorize("@ss.hasPermi('system:usermanage:edit')")
    @Log(title = "用户信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Usermanage usermanage)
    {
        return toAjax(usermanageService.updateUsermanage(usermanage));
    }

    /**
     * 删除用户信息
     */
    @PreAuthorize("@ss.hasPermi('system:usermanage:remove')")
    @Log(title = "用户信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(usermanageService.deleteUsermanageByUserIds(userIds));
    }
}
