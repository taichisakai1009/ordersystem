package com.example.demo.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.Dto.SeeClerksDto;
import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Repository.ClerksRepository;
import com.example.demo.service.AdminService;
import com.example.demo.service.ClerksService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final HttpSession session;
	private final ClerksService clerksService;
	private final AdminService adminService;
	private final ClerksRepository clerksRepository;

	public AdminController(HttpSession session, ClerksService clerksService, AdminService adminService, ClerksRepository clerksRepository) {
		this.session = session;
		this.clerksService = clerksService;
		this.adminService = adminService;
		this.clerksRepository = clerksRepository;
	}

	// 管理者画面表示
	@PreAuthorize("hasRole('Admin')")
	@RequestMapping(path = "/admin")
	public String showAdminPage(Model model) {
		ClerksEntity clerk = (ClerksEntity) session.getAttribute("clerk");
		model.addAttribute("clerk", clerk);
		return "admin/admin";
	}

	// 店員選択画面表示
	@PreAuthorize("hasRole('Admin')")
	@RequestMapping(path = "/clerkSelect", params = "show")
	public String showClerkSelect(Model model) {
		List<ClerksEntity> clerksEntity = clerksRepository.findAll();
		List<SeeClerksDto> clerks = clerksService.setSeeClerksDtoList(clerksEntity);
		model.addAttribute("clerks", clerks);
		return "admin/clerkSelect";
	}
	
	// 店員番号で従業員を検索
	@RequestMapping(path = "/clerkSelect", params = "numberSearch")
	@ResponseBody // JSONレスポンスを返すために追加
	public List<ClerksEntity> searchClerksByNumber(@RequestParam Integer clerkNumber) {
	    return adminService.findClerksByNumber(clerkNumber);
	}
	
	// 氏名（あいまい検索）で従業員を検索
	@RequestMapping(path = "/clerkSelect", params = "nameSearch")
	@ResponseBody // JSONレスポンスを返すために追加
	public List<ClerksEntity> searchClerksByName(@RequestParam String name) {
	    return adminService.findByNameContaining(name);
	}
	
	// 店員管理画面表示
	@RequestMapping(path = "/clerkManagement", params = "show")
	public String showClerkManagement(String name, Integer clerkNumber, String mailAddress, String tel, LocalDate startDate, String roleName, Model model) {
		model.addAttribute("name", name);
		model.addAttribute("clerkNumber", clerkNumber);
		model.addAttribute("mailAddress", mailAddress);
		model.addAttribute("tel", tel);
		model.addAttribute("startDate", startDate);
		model.addAttribute("roleName", roleName);
		return "admin/clerkManagement";
	}
}
