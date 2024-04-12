package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller //컨트롤러로 인식
public class BoardController {

    @Autowired  //의존성 주입.
    private BoardService boardService;

    @GetMapping("/board/write")    //게시글 작성 폼을 보여주는 역할
    public String boardWriteForm() {
        return "boardwrite";
    }

    @PostMapping("/board/writepro") //게시글 작성을 처리하고 작성 완료 메시지와 함께 메시지 페이지로 이동
    public String boardWritePro(Board board, Model model, @RequestParam("file") MultipartFile file) throws Exception{

        boardService.write(board, file);

        model.addAttribute("message", "글 작성 완료");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }







    @GetMapping("/board/list")  //게시글 목록을 보여주는 페이지로 이동
    public String boardList(Model model, @PageableDefault(page = 0, size = 10, sort =  "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Board> list = boardService.boardList(pageable);

        int nowPage = list.getPageable().getPageNumber();
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardlist";
    }

    @GetMapping("/board/view") // localhost:8080/board/view?id=1 특정 게시글의 내용을 보여주는 페이지로 이동
    public String boardView(Model model, @RequestParam("id") Integer id) {

        model.addAttribute("board", boardService.boardView(id));

        return "boardview";
    }

    @GetMapping("/board/delete")    //특정 게시글을 삭제하고, 게시글 목록 페이지로 리다이렉트
    public String boardDelete(@RequestParam("id") Integer id) {
        boardService.boardDelete(id);

        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}")   //특정 게시글을 수정하기 위한 수정 폼
    public String boardModify(@PathVariable("id") Integer id, Model model) {

        model.addAttribute("board", boardService.boardView(id));

        return "boardmodify";
    }

    @PostMapping("/board/update/{id}")  //특정 게시글을 수정하고, 수정 완료 메시지와 함께 메시지 페이지로 이동
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model,@RequestParam("file") MultipartFile file) throws Exception {

        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        boardService.write(boardTemp, file);

        model.addAttribute("message", "글 수정 완료");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }

}
