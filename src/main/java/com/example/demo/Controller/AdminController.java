package com.example.demo.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.Dto.ClerkDetailsDto;
import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Repository.ClerksRepository;
import com.example.demo.service.AdminService;
import com.example.demo.service.ClerksService;

import jakarta.mail.MessagingException;
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
	@ResponseBody
	public ResponseEntity<Map<String, String>> updateClerkDetails(@RequestBody ClerkDetailsDto dto) {
		Integer clerkId = dto.getClerkId();
		String myMailAddress = clerksService.getMailAddressByClerkId(clerkId);
		String name = dto.getName();
		String mailAddress = dto.getMailAddress();
		String tel = dto.getTel();
		
		Map<String, String> response = new HashMap<>();
		boolean addressDuplication = !mailAddress.equals(myMailAddress) && adminService.existsByMailAddress(mailAddress);

		if (addressDuplication) {
			response.put("message", "このメールアドレスはすでに使われています。");
		} else {
			adminService.updateClerkDetails(clerkId, name, mailAddress, tel);
			ClerksEntity clerk = (ClerksEntity) session.getAttribute("clerk"); // セッション内容の更新
			clerk.setName(name);
			clerk.setMailAddress(mailAddress);
			clerk.setTel(tel);
			session.setAttribute("clerk", clerk);
			response.put("message", "内容を更新しました。");
		}
		System.out.println("response：" + response);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<?> test() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(null, null);
		return ResponseEntity.ok(null);
	}

	// 店員情報の削除
	@Transactional
	@RequestMapping(path = "/clerkSelect", params = "delete")
	public String deleteByClerkId(Integer clerkId) {
		adminService.deleteByClerkId(clerkId);
		System.out.println("店員情報の削除。clerkId = " + clerkId);
		return "clerks/clerkSelect";
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
			String startDateStr, Integer roleId, Model model) throws MessagingException {
		boolean addressDuplication = adminService.existsByMailAddress(mailAddress);
		boolean validationFlg = adminService.registValidationCheck(name, mailAddress, tel, model); // バリデーションチェック
		if (addressDuplication) {
			model.addAttribute("emailDupplication", "このメールアドレスはすでに使われています。");
			return "admin/clerkRegist";
		} else if (!validationFlg) {
			System.out.println("バリデーションエラー");
			return "admin/clerkRegist";
		} else {
			ClerksEntity clerksEntity = adminService.registClerk(name, clerkNumber, rawPassword, mailAddress, tel,
					startDateStr, roleId);
			model.addAttribute("registComfirm", name + "さんの新規登録を行いました。");
			System.out.println("新規登録：" + clerksEntity);
			return "admin/clerkRegist";
		}
	}

}
