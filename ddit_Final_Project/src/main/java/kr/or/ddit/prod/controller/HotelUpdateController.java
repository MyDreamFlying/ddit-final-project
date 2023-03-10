package kr.or.ddit.prod.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.account.service.AccountService;
import kr.or.ddit.member.company.controller.CompanyProducts;
import kr.or.ddit.member.company.service.CompanyMemberService;
import kr.or.ddit.member.company.service.CompanyService;
import kr.or.ddit.prod.hotel.service.HotelService;
import kr.or.ddit.prod.rentcar.service.RentcarService;
import kr.or.ddit.prod.service.ProdService;
import kr.or.ddit.vo.AccountVO;
import kr.or.ddit.vo.AccountVOWrapper;
import kr.or.ddit.vo.AreaVO;
import kr.or.ddit.vo.CategoryVO;
import kr.or.ddit.vo.CompanyInfoVO;
import kr.or.ddit.vo.CompanyMemberVO;
import kr.or.ddit.vo.HotelRoomVO;
import kr.or.ddit.vo.PersonalMemberVO;
import kr.or.ddit.vo.ProdImageVO;
import kr.or.ddit.vo.ProdVO;
import kr.or.ddit.vo.ProfileImageVO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ui.Model;

@Slf4j
@Controller
@RequestMapping("/mypage/member/company/product/HotelRoomUpdate")
public class HotelUpdateController {
	@Inject
	private CompanyService cService;
	@Inject
	private RentcarService rcService;
	@Inject
	private HotelService hService;
	@Inject
	private AccountService aSerice;
	@Inject
	private ProdService pService;
	@Inject
	private CompanyMemberService cmService;

	@Value("#{appInfo.prodImageFolder}")
	private String imageFolderURL;
	@Value("#{appInfo.prodImageFolder}")
	private Resource imageFolderRes;
	private File saveFolder;

	@PostConstruct
	public void init() throws IOException {
		saveFolder = imageFolderRes.getFile();
		if (!saveFolder.exists())
			saveFolder.mkdirs();
	}

	String catuCode = "HRCT";
	File save;
	File getFileName;

	// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<?????????????????????
	// ???>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@GetMapping
	public String updateHotelProdView(@RequestParam(name = "what", required = true) int prodNum, Model model,
			@AuthenticationPrincipal AccountVOWrapper accountWrapper,
			@AuthenticationPrincipal(expression = "realUser") AccountVO account

	) {

		String viewName = null;

		// ?????????????????? ????????? ????????????
		AccountVO test1 = accountWrapper.getRealUser();
		String comId = test1.getAccId();

		// ??????????????? ???????????? ??????????????? ???????????? ??????
		AccountVO accVO = aSerice.selectAccount(comId);
		String accRole = accVO.getMemCode();

		// ?????? ?????? ?????? ????????? ????????????.

		ProdVO category = pService.sliceCategory(prodNum);

		// cInfoNum ????????????
		CompanyInfoVO companyInfo = pService.SelectCompanyInfo(comId);

		HotelRoomVO hotelRoomVO = hService.hotelRoomDetail(prodNum);
		List<ProdImageVO> prodImageList = hService.retrieveHotelImage(prodNum);

		String majorImage = "";
		
		if (prodImageList.size() != 0) {
			majorImage = "/rest4Trip" + imageFolderURL + "/" + prodImageList.get(0).getPImgName();
			if (prodImageList.get(0).getPImgName().length() == 0) {
				majorImage = "/rest4Trip/resources/images/No_image";
			}	
		}



		List<String> prodImgName = new ArrayList<String>();

		for (ProdImageVO vo : prodImageList) {
			prodImgName.add(vo.getPImgName());
		}

		// ???????????? ????????????
		CompanyMemberVO memberVO = cmService.SelectCompanyMember(comId);
		String str = memberVO.getComAddr();
		String areaName = str.substring(0, 2);
		AreaVO area = pService.selectAreaCode(areaName);
		String areaCode = area.getAreaCode();

		List<CategoryVO> categoryList = pService.CategoryList(catuCode);

		model.addAttribute("prodImgName", prodImgName);
		model.addAttribute("categoryList", categoryList);
		model.addAttribute("category", category);
		model.addAttribute("companyInfo", companyInfo);
		model.addAttribute("hotelRoomVO", hotelRoomVO);

		return "myPage/member/company/product/hotelUpdate";
	}

	// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<????????????>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@PostMapping
	public String updateHote(Model model, @ModelAttribute("hotelRoomVO") HotelRoomVO hotelRoomVO,
			@RequestPart(name = "multipartImage") List<MultipartFile> multipartImage, @ModelAttribute ProdVO prodVO,
			@ModelAttribute AreaVO areaVO, @RequestParam(name = "what", required = true) int prodNum,
			HttpServletRequest request) throws IOException {

		int romNum = prodNum;
		// ???????????? ????????? ????????????.
		int rowcnt = pService.SearchProdImge(prodNum);
		// ?????? ????????? ????????? ?????? prodImageVO??? ?????? ???????????? ???????????? ?????????
		List<ProdImageVO> prodImage = pService.retrieveProdImage(prodNum);

		save = new File(request.getSession().getServletContext().getRealPath(imageFolderURL));
		// ????????? ?????? ???????????? ????????? ??????
		for (int i = 0; i < multipartImage.size(); i++) {
			ProdImageVO prodImageTemp = new ProdImageVO(multipartImage.get(i));
			String orgImgName = prodImage.get(i).getPImgName();
			String newImgName = prodImageTemp.getPImgName();

			if (multipartImage.get(i) == null) {

			} else {
				save = new File(request.getSession().getServletContext().getRealPath(imageFolderURL));
				// ?????? ????????? ?????? ?????? ??????
				getFileName = new File(
						request.getSession().getServletContext().getRealPath(imageFolderURL) + "/" + orgImgName);

				if (!orgImgName.equals(newImgName)) {
					int imgDeleteNum = pService.deletePrImg(orgImgName);
					getFileName.delete();

					if (imgDeleteNum > 0) {
						int imgInsertNum = pService.changeInsertProdImages(prodImageTemp);
						if (imgInsertNum > 0) {
							prodImageTemp.saveTo(save);
						}

					}
				}
			}

		}

		ProdVO selectProd = pService.selectProdvo(prodNum);
		selectProd.setProdNam(prodVO.getProdNam());
		selectProd.setProdQty(prodVO.getProdQty());
		selectProd.setProdCode(prodVO.getProdCode());

		int prodUpdateNum = pService.hoteMainlUpdate(selectProd);
		hotelRoomVO.setRomNum(romNum);
		int hotelUpdateNum = hService.hotelOptionlUpdate(hotelRoomVO);

		List<ProdImageVO> prodImageList = hService.retrieveHotelImage(prodNum);
		String majorImage = "";
		for (int i = 0; i <= 0; i++) {
			majorImage = prodImageList.get(i).getPImgName();
		}
		List<String> prodImgName = new ArrayList<String>();

		for (ProdImageVO vo : prodImageList) {
			prodImgName.add(vo.getPImgName());
		}

		model.addAttribute("prodNum", prodNum);
		model.addAttribute("majorImage", majorImage);
		model.addAttribute("prodImgName", prodImgName);
		model.addAttribute("selectProd", selectProd);
		model.addAttribute("hotelRoomVO", hotelRoomVO);

		return "myPage/member/company/product/hotelDetailView";
	}

}
