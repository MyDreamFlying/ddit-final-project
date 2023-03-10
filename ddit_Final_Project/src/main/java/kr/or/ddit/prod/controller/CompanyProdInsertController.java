package kr.or.ddit.prod.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.support.incrementer.HsqlSequenceMaxValueIncrementer;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.account.service.AccountService;
import kr.or.ddit.member.company.service.CompanyMemberService;
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
import kr.or.ddit.vo.ProdImageVO;
import kr.or.ddit.vo.ProdVO;
import kr.or.ddit.vo.RentCarVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
//@RequestMapping("/prod/Insert")
public class CompanyProdInsertController {
	@Inject
	private final HotelService hService;
	@Inject
	private final ProdService pService;
	@Inject
	private final AccountService aSerice;
	@Inject
	private final CompanyMemberService cmService;
	@Inject
	private final RentcarService rService;

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

	@ModelAttribute("hotel")
	public HotelRoomVO hotelRoom() {

		return new HotelRoomVO();
	}

	String hotelCatuCode = "HRCT";
	String carCatuCode = "RCCT";
	String colorCatuCode = "COLOR";
	String carFuelCode = "RCFUEL";
	String carGear = "CARGEAR";
	String carBrand = "CARBRND";
	String areaName="";

	String majorImage = "";
	String prodImgName = "";

	@RequestMapping(value = "/prod/Insert", method = RequestMethod.GET)
	public String ProdFrom(Model model, @AuthenticationPrincipal AccountVOWrapper accountWrapper,
			@AuthenticationPrincipal(expression = "realUser") AccountVO account, ProdVO prodVO) {
		String viewName = null;

		// ?????????????????? ????????? ????????????
		AccountVO test1 = accountWrapper.getRealUser();
		String comId = test1.getAccId();

		// ??????????????? ???????????? ??????????????? ???????????? ??????
		AccountVO accVO = aSerice.selectAccount(comId);
		String accRole = accVO.getMemCode();

		// cInfoNum ????????????
		CompanyInfoVO companyInfo = pService.SelectCompanyInfo(comId);


		List<CategoryVO> hotelCategoryList = pService.CategoryList(hotelCatuCode);
		List<CategoryVO> rentCarCategoryList = pService.CategoryList(carCatuCode);
		List<CategoryVO> carColorCategoryList = pService.CategoryList(colorCatuCode);
		List<CategoryVO> carFuelCategoryList = pService.CategoryList(carFuelCode);
		List<CategoryVO> carGearCategoryList = pService.CategoryList(carGear);
		List<CategoryVO> carBrandCategoryList = pService.CategoryList(carBrand);
		List<AreaVO> areaList=pService.findArea();
		if (accRole.equals("ROLE_HC")) {

			String comCode = accRole.substring(accRole.length() - 2);
			model.addAttribute("categoryList", hotelCategoryList);
			model.addAttribute("companyInfo", companyInfo);
			model.addAttribute("comCode", comCode);
			model.addAttribute("areaList", areaList);

			viewName = "myPage/member/company/product/hotelInsertForm";

		} else {
			String comCode = accRole.substring(accRole.length() - 2);
			model.addAttribute("carBrandCategoryList", carBrandCategoryList);
			model.addAttribute("carGearCategoryList", carGearCategoryList);
			model.addAttribute("categoryList", rentCarCategoryList);
			model.addAttribute("carColorCategoryList", carColorCategoryList);
			model.addAttribute("carFuelCategoryList", carFuelCategoryList);
			model.addAttribute("companyInfo", companyInfo);
			model.addAttribute("comCode", comCode);
			model.addAttribute("areaList", areaList);

			viewName = "myPage/member/company/product/rentCarInsertForm";
		}

		return viewName;
	}

	/*
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 * >
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<??????????????????>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 * >>>
	 */
	@RequestMapping(value = "/prod/Insert", method = RequestMethod.POST)
	public String prodHotelInsert(@AuthenticationPrincipal AccountVOWrapper accountWrapper
			, @AuthenticationPrincipal(expression = "realUser") AccountVO account
			, @ModelAttribute HotelRoomVO hotelRoomVO
			, @ModelAttribute RentCarVO rentCarVO
			, @ModelAttribute ProdVO prodVO
			, @ModelAttribute AreaVO areaVO
			,Model model, @RequestPart(name = "multipartImage") List<MultipartFile> multipartImage) {

		String viewName = null;

		// ?????????????????? ????????? ????????????
		AccountVO test1 = accountWrapper.getRealUser();
		String comId = test1.getAccId();


		// ??????????????? ???????????? ??????????????? ???????????? ??????
		AccountVO accVO = aSerice.selectAccount(comId);
		String accRole = accVO.getMemCode();
		log.info("com Role: {}", accRole);

		// cInfoNum ????????????
		CompanyInfoVO companyInfo = pService.SelectCompanyInfo(comId);

		
//		log.info("cinfoNum: {}", cInfoNum);

		// prodNum ?????? ?????? ?????????
		int prodNum = pService.getProdNum();
		log.info("prodNum: {}", prodNum);

		// prodvo??? ??????????????? ????????? ??????
		prodVO.setProdImages(multipartImage);
		// ???????????? prodNum ????????????
		prodVO.setProdNum(prodNum);
//		prodVO.setCinfoNum(companyInfo.getCinfoNum());
		prodVO.setComId(comId);

		for (MultipartFile mpfSize : multipartImage) {
			// ?????? ???????????? prodvo??? ?????? ????????? ????????? ??????.
			int prodRowcnt = pService.createProd(prodVO);
			int insertProdNum = prodVO.getProdNum();
			
//////////////////////////////????????? ???????????? ???????????? ????????????/////////////////////////
			if (mpfSize.getSize() == 0) {
				if (accRole.equals("ROLE_RC")) {
					// ?????? ???????????? rentCarVO ??? ??? ????????? ?????? ????????? ??????.
					rentCarVO.setProdNum(insertProdNum);
					//?????????????????? ????????????.
					int carRowCnt=rService.createRentCarProduct(rentCarVO);
					viewName = "myPage/member/company/product/rentCarDetailView";
					
				}else {
					// ?????? ???????????? hotelRoomVO ??? ??? ????????? ?????? ????????? ??????.
					hotelRoomVO.setRomNum(insertProdNum);
					// ???????????? ???????????? ?????????????????? ??????????????? ????????? ?????????.
					String roomName = prodVO.getProdNam();
					hotelRoomVO.setRomNam(roomName);
					// ?????? ????????? ????????????.
					int hotelRowcnt = hService.createHotelProduct(hotelRoomVO);
					viewName = "myPage/member/company/product/hotelDetailView";
				}
				
				List<AreaVO> areaList = pService.selectArea(areaVO);

				break;

////////////////////////////???????????? ???????????????/////////////////////////				
			} else {

				for (ProdImageVO vo : prodVO.getProdImageList()) {
					log.info("getProdNum: {}", vo.getProdNum());
					vo.setProdNum(prodVO.getProdNum());
				}

				// ???????????? ????????????
				System.out.println(prodRowcnt);

				// ???????????? ????????????
				List<AreaVO> areaList = pService.selectArea(areaVO);

				log.info("????????? ???????????? ????????? ?????????: {}", areaList);
				List<ProdImageVO> prodImageList = pService.findThumbnail(prodVO.getProdNum());
				log.info("prodImageList // findThumnail: {}", prodVO.getProdNum());

				String thumbNail = prodImageList.get(0).getPImgName();
				prodVO.setProdImg("/rest4Trip" + imageFolderURL + "/" + thumbNail);

				int thumNailCount = pService.insertThumbNail(prodVO);
				System.out.println(thumNailCount);
				
				String majorImage = "";
				
				
////////////////////////////////???????????? ???????????????////////////////////////////////////////
				if (accRole.equals("ROLE_RC")) {
					rentCarVO.setProdNum(insertProdNum);
					int carRowCnt=rService.createRentCarProduct(rentCarVO);
					
					if (carRowCnt > 0) {
						for (int i = 0; i <= 0; i++) {
							majorImage = "/rest4Trip"+imageFolderURL+"/"+prodImageList.get(i).getPImgName();
						}
						List<String> prodImgName = new ArrayList<String>();
						
						for (ProdImageVO vo : prodImageList) {
							prodImgName.add(vo.getPImgName());
						}
						viewName = "myPage/member/company/product/rentCarDetailView";
						model.addAttribute("majorImage", majorImage);
						model.addAttribute("prodImgName", prodImgName);
						model.addAttribute("prodVO", prodVO);
						model.addAttribute("rentCarVO", rentCarVO);
					}else {
						
					}
					// ?????? ???????????? hotelRoomVO  ?????? ??????.
					
//////////////////////////////????????? ???????????????////////////////////////////////////////				
				} else if (accRole.equals("ROLE_HC")) {
					// ?????? ???????????? hotelRoomVO  ?????? ??????.
					hotelRoomVO.setRomNum(insertProdNum);
					
					// ???????????? ???????????? ?????????????????? ??????????????? ????????? ?????????.
					String roomName = prodVO.getProdNam();
					hotelRoomVO.setRomNam(roomName);
					int hotelRowcnt = hService.createHotelProduct(hotelRoomVO);
					
					if (hotelRowcnt > 0) {						
						for (int i = 0; i <= 0; i++) {
							majorImage ="/rest4Trip"+imageFolderURL+"/"+prodImageList.get(i).getPImgName();
						}
						List<String> prodImgName = new ArrayList<String>();

						for (ProdImageVO vo : prodImageList) {
							prodImgName.add(vo.getPImgName());
						}
						viewName = "myPage/member/company/product/hotelDetailView";
						model.addAttribute("majorImage", majorImage);
						model.addAttribute("prodImgName", prodImgName);
						model.addAttribute("prodVO", prodVO);
						model.addAttribute("hotelRoomVO", hotelRoomVO);
					} else {

					}
				} else {
					model.addAttribute("majorImage", majorImage);
					viewName = "/mypage/member/company/product/detail/package";
				}
				break;
			}
		}
		return viewName;
	}

}
