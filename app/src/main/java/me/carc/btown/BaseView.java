package me.carc.btown;

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);
}
