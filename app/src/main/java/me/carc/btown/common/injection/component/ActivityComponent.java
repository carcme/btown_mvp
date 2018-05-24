package me.carc.btown.common.injection.component;

import dagger.Subcomponent;
import me.carc.btown.common.injection.PerActivity;
import me.carc.btown.common.injection.module.ActivityModule;
import me.carc.btown.tours.externalLinks.ExternalLinksActivity;
import me.carc.btown.ui.front_page.FrontPageActivity;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(FrontPageActivity frontPageActivity);
    void inject(ExternalLinksActivity externalLinksActivity);

}
