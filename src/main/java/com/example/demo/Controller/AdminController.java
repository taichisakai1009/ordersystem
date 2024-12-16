package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.Dto.ClerkDetailsDto;
import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Repository.ClerksRepository;
import com.example.demo.service.AdminService;
import com.example.demo.service.ClerksService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final HttpSession session;
	private final ClerksService clerksService;
	private final AdminService adminService;
	private final ClerksRepository clerksRepository;

	public AdminController(HttpSession session, ClerksService clerksService, AdminService adminService,
			ClerksRepository clerksRepository) {
		this.session = session;
		this.clerksService = clerksService;
		this.adminService = adminService;
		this.clerksRepository = clerksRepository;
	}


	// 店員情報の更新
	@RequestMapping(path = "/clerkSelect", params = "update", method = RequestMethod.POST)
	public void updateClerkDetails(@RequestBody ClerkDetailsDto dto) {
		adminService.updateClerkDetails(dto.getClerkId(), dto.getName(), dto.getMailAddress(), dto.getTel());
	}

	// 店員情報の削除
	@Transactional
	@RequestMapping(path = "/clerkSelect", params = "delete")
	public String deleteByClerkId(Integer clerkId) {
		adminService.deleteByClerkId(clerkId);
		System.out.println("店員情報の削除。clerkId = " + clerkId);
		return "admin/clerkSelect";
	}

	//　店員登録画面表示
	@RequestMapping(path = "/clerkRegist", params = "show")
	public String showClerkRegist() {
		return "admin/clerkRegist";
	}

	// 登録する店員番号がすでに使われていないか調べる
	@RequestMapping(path = "/clerkRegist", params = "duplication")
	@ResponseBody // JSONレスポンスを返すために追加 @PathVariable 
	public boolean existsByClerkNumber(Integer clerkNumber) {
		return adminService.existsByClerkNumber(clerkNumber);
	}

	// 店員の登録
	@RequestMapping(path = "/clerkRegist", params = "regist")
	public String registClerk(String name, Integer clerkNumber, String rawPassword, String mailAddress, String tel,
			String startDateStr, Integer roleId) {
		ClerksEntity clerksEntity = adminService.registClerk(name, clerkNumber, rawPassword, mailAddress, tel, startDateStr, roleId);
		System.out.println("新規登録：" + clerksEntity);
		return "admin/clerkRegist";
	}

}
