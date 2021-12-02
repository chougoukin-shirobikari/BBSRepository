package com.example.bulletinboard.service;

import org.springframework.stereotype.Service;

import com.example.bulletinboard.model.PageInfo;

import lombok.RequiredArgsConstructor;

/**
 * ページングの処理に関するサービスクラス
 *
 */
@Service
@RequiredArgsConstructor
public class PaginationService {
	
	//現在のページが最初であるかをチェック
	public boolean isFirst(int currentpage) {
		boolean isFirst = false;
		if(currentpage == 0) {
			isFirst = true;
		}
		return isFirst;
	}
	
	//現在のページが最後であるかをチェック
	public boolean isLast(int currentpage, int totalpage) {
		boolean isLast = false;
		if(currentpage + 1 == totalpage) {
			isLast = true;
		}
		return isLast;
	}
	
	//総ページ数を取得
	public int getTotalPage(int total, int limit) {
		int totalpage = (total % limit == 0 ? total / limit : total / limit +1);
		return totalpage;
	}
	
	//ページングに必要なパラメータを作成
	public PageInfo createPageInfo(Integer page, int total, int limit) {
		int totalpage = getTotalPage(total, limit);
		boolean isFirst = isFirst(page);
		boolean isLast = isLast(page, totalpage);
		PageInfo pageInfo = new PageInfo(limit, page, total, totalpage, isFirst, isLast);
		return pageInfo;
	}

}



















