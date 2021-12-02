package com.example.bulletinboard.validator;

import javax.validation.GroupSequence;

/**
 * バリデーションの優先順位を定義したinterface
 *
 */
@GroupSequence({First.class, Second.class})
public interface All {

}
