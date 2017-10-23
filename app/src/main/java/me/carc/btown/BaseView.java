package me.carc.btown;

/**
 * Created by nawin on 1/6/17.
 */

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);
}
