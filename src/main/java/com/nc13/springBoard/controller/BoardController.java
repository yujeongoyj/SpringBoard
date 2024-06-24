package com.nc13.springBoard.controller;

import com.nc13.springBoard.model.BoardDTO;
import com.nc13.springBoard.model.ReplyDTO;
import com.nc13.springBoard.model.UserDTO;
import com.nc13.springBoard.service.BoardService;
import com.nc13.springBoard.service.ReplyService;
import com.nc13.springBoard.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



@Controller
@RequestMapping("/board/")
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private ReplyService replyService;


    @GetMapping("showAll")
    public String moveToFirstPage() {
        return "redirect:/board/showAll/1";
    }

    @GetMapping("showAll/{pageNo}")
    public String showAll(HttpSession session, Model model, @PathVariable int pageNo) {
        UserDTO logIn = (UserDTO) session.getAttribute("logIn");
        if (logIn == null) {
            return "redirect:/";
        }
        // 가장 마지막 페이지의 번호
        int maxPage = boardService.selectMaxPage();
        model.addAttribute("maxPage", maxPage);

        // 우리가 이제 pageNo를 사용하여
        // 시작 페이지 번호
        // 끝 페이지 번호
        // 을 계산해 주어야 한다.
        // 이때에는 크게 3가지가 있다.
        // 1. 현재 페이지가 3 이하일 경우
        // 시작: 1, 끝: 5
        // 2. 현재 페이지가 최대 페이지 -2 이상일 경우
        // 시작: 최대 페이지 -4 끝: 최대 페이지
        // 3. 그외
        // 시작: 현재 페이지 -2 끝: 현재 페이지 +2

        // 시작 페이지
        int startPage;

        // 끝 페이지
        int endPage;

        if (maxPage < 5) {
            startPage = 1;
            endPage = maxPage;
        } else if (pageNo <= 3) {
            startPage = 1;
            endPage = 5;
        } else if (pageNo >= maxPage - 2) {
            startPage = maxPage - 4;
            endPage = maxPage;
        } else {
            startPage = pageNo - 2;
            endPage = pageNo + 2;
        }

        model.addAttribute("curPage", pageNo);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);


        List<BoardDTO> list = boardService.selectAll(pageNo);
        model.addAttribute("list", list);


        return "board/showAll";
    }

    @GetMapping("write")
    public String showWrite(HttpSession session) {
        UserDTO logIn = (UserDTO) session.getAttribute("logIn");
        if (logIn == null) {
            return "redirect:/";
        }

        return "board/write";
    }

    @PostMapping("write")
    public String write(HttpSession session, BoardDTO boardDTO, MultipartFile[] file) {
        // MultipartFile 은 파일 여러개 업로드 가능, 때문에 배열

        UserDTO logIn = (UserDTO) session.getAttribute("logIn");
        if (logIn == null) {
            return "redirect:/";
        }

        boardDTO.setWriterId(logIn.getId());

        String path = "c:\\uploads";

        File pathDir = new File(path);
        if(!pathDir.exists()) {
            pathDir.mkdirs();     // 사용자가 업로드할 때 해당 파일의 경로와 이름을 지정
        }


        try {
            for(MultipartFile mf: file) {
                File f = new File(path, mf.getOriginalFilename());
                mf.transferTo(f); // 파일을 저 목적지로 옮기는 과정
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //          boardService.insert(boardDTO);

        return "redirect:/board/showOne/" + boardDTO.getId();
    }

    // 우리가 주소창에 있는 값을 매핑해줄 수 있다.
    @GetMapping("showOne/{id}")
    public String showOne(HttpSession session, @PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        UserDTO logIn = (UserDTO) session.getAttribute("logIn");
        if (logIn == null) {
            return "redirect:/";
        }

        BoardDTO boardDTO = boardService.selectOne(id);

        if (boardDTO == null) {
            redirectAttributes.addFlashAttribute("message", "해당 글 번호는 유효하지 않습니다.");
            return "redirect:/showMessage";
        }

        List<ReplyDTO> replyList = replyService.selectAll(id);

        model.addAttribute("boardDTO", boardDTO);
        model.addAttribute("replyList", replyList);

        return "board/showOne";
    }

    @GetMapping("update/{id}")
    public String showUpdate(@PathVariable int id, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        UserDTO logIn = (UserDTO) session.getAttribute("logIn");
        if (logIn == null) {
            return "redirect:/";
        }

        BoardDTO boardDTO = boardService.selectOne(id);

        if (boardDTO == null) {
            redirectAttributes.addFlashAttribute("message", "존재하지 않는 글 번호입니다.");
            return "redirect:/showMessage";
        }

        if (boardDTO.getWriterId() != logIn.getId()) {
            redirectAttributes.addFlashAttribute("message", "권한이 없습니다.");
            return "redirect:/showMessage";
        }

        model.addAttribute("boardDTO", boardDTO);

        return "board/update";

    }

    @PostMapping("update/{id}")
    public String update(@PathVariable int id, HttpSession session, RedirectAttributes redirectAttributes, BoardDTO attempt) {
        UserDTO logIn = (UserDTO) session.getAttribute("logIn");
        if (logIn == null) {
            return "redirect:/";
        }

        BoardDTO boardDTO = boardService.selectOne(id);
        if (boardDTO == null) {
            redirectAttributes.addFlashAttribute("message", "유효하지 않은 글 번호입니다.");
            return "redirect:/showMessage";
        }

        if (logIn.getId() != boardDTO.getWriterId()) {
            redirectAttributes.addFlashAttribute("message", "권한이 없습니다.");
            return "redirect:/showMessage";
        }

        attempt.setId(id);

        boardService.update(attempt);


        return "redirect:/board/showOne/" + id;
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable int id, HttpSession session, RedirectAttributes redirectAttributes) {
        UserDTO logIn = (UserDTO) session.getAttribute("logIn");
        if (logIn == null) {
            return "redirect:/";
        }

        BoardDTO boardDTO = boardService.selectOne(id);
        if (boardDTO == null) {
            redirectAttributes.addFlashAttribute("message", "존재하지 않는 글번호");
            return "redirect:/showMessage";
        }

        if (boardDTO.getWriterId() != logIn.getId()) {
            redirectAttributes.addFlashAttribute("message", "권한 없음");
            return "redirect:/showMessage";
        }

        boardService.delete(id);


        return "redirect:/board/showAll";
    }

    // 일반 컨트롤러 안에
    // Restful API로써, JSON의 결과값을 리턴해야하는 경우ㅡ
    // 맵핑 어노테이션 위에 ResponseBody 어노테이션을 붙여준다.
    @ResponseBody
    @PostMapping("uploads")
    public Map<String, Object> uploads(MultipartHttpServletRequest request){
        Map<String, Object> resultMap=new HashMap<>();

        String uploadPath="";

        MultipartFile file = request.getFile("upload");
        String fileName=file.getOriginalFilename();
        String extension=fileName.substring(fileName.lastIndexOf("."));// 파일 이름 보면 다운로드/jpg , 내음악.mp3 이런거 찾아내는 애
        String uploadName = UUID.randomUUID() + extension;

        String realPath=request.getServletContext().getRealPath("/board/uploads/");//돌아가는 톰캣의 실제 주소 찾는 메서드
        Path realDir= Paths.get(realPath);
        if(!Files.exists(realDir)){
            try{
                Files.createDirectories(realDir);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        File uploadFile = new File(realPath+uploadName);
        try {
            file.transferTo(uploadFile);
        } catch (IOException e){
            System.out.println("파일 전송 중 에러");
            e.printStackTrace();
        }

        // 업로드 경로의 내용
        uploadPath = "/board/uploads/"+uploadName;

        resultMap.put("uploaded",true);
        resultMap.put("url", uploadPath);
        return resultMap;
    }

   /*     @GetMapping("test")
    public String test(HttpSession session) {
        UserDTO logIn = (UserDTO) session.getAttribute("logIn");
        if (logIn == null) {
            return "redirect:/";
        }
        for (int i = 1; i <= 300; i++) {
            BoardDTO boardDTO = new BoardDTO();
            boardDTO.setTitle("테스트 제목 " + i);
            boardDTO.setContent("테스트 " + i + "번 글의 내용입니다.");
            boardDTO.setWriterId(logIn.getId());
            boardService.insert(boardDTO);
        }

        return "redirect:/board/showAll";
    }*/
}

















