package org.de_studio.recentappswitcher.edgeService;

import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;

/**
 * Created by HaiNguyen on 12/23/16.
 */

public class NewServicePresenter extends BasePresenter<NewServicePresenter.View, NewServiceModel> {
    public NewServicePresenter(NewServiceModel model) {
        super(model);
    }

    public interface View extends PresenterView {

    }
}
