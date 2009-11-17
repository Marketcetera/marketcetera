package org.marketcetera.photon.commons.ui.databinding;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ui.databinding.RequiredFieldSupport.RequiredStatus;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link CustomWizardPageSupport}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(SimpleUIRunner.class)
public class CustomWizardPageSupportTest {

    private DataBindingContext mDataBindingContext;
    private WizardPage mWizardPage;
    private CustomWizardPageSupport mFixture;
    private WizardDialog mWizardDialog;

    @Before
    @UI
    public void before() {
        mDataBindingContext = new DataBindingContext();
        mWizardPage = new WizardPage("abc") {
            @Override
            public void createControl(Composite parent) {
                mFixture = CustomWizardPageSupport.create(this,
                        mDataBindingContext);
                setControl(new Composite(parent, SWT.NONE));
            }
        };
        mWizardDialog = new WizardDialog(null, new Wizard() {
            @Override
            public void addPages() {
                addPage(mWizardPage);
            }

            @Override
            public boolean performFinish() {
                return false;
            }
        });
        mWizardDialog.setBlockOnOpen(false);
    }

    @After
    @UI
    public void after() {
        mWizardDialog.close();
        mFixture.dispose();
        mDataBindingContext.dispose();
    }

    @Test
    @UI
    public void testRequiredStatusDoesNotTriggerError() {
        final WritableValue status = new WritableValue(new RequiredStatus("required"), IStatus.class);
        final MultiValidator validator = new MultiValidator() {
            @Override
            protected IStatus validate() {
                return (IStatus) status.getValue();
            }
        };
        mDataBindingContext.addValidationStatusProvider(validator);
        mWizardDialog.open();
        assertThat(mWizardPage.getErrorMessage(), nullValue());
        assertThat(mWizardPage.isPageComplete(), is(false));
        status.setValue(ValidationStatus.ok());
        assertThat(mWizardPage.getErrorMessage(), nullValue());
        assertThat(mWizardPage.isPageComplete(), is(true));
        status.setValue(new RequiredStatus("required2"));
        assertThat(mWizardPage.getErrorMessage(), nullValue());
        assertThat(mWizardPage.isPageComplete(), is(false));
    }

    @Test
    @UI
    public void testRegularErrorIsShown() {
        final WritableValue status = new WritableValue(ValidationStatus.error("error"), IStatus.class);
        final MultiValidator validator = new MultiValidator() {
            @Override
            protected IStatus validate() {
                return (IStatus) status.getValue();
            }
        };
        mDataBindingContext.addValidationStatusProvider(validator);
        mWizardDialog.open();
        assertThat(mWizardPage.getErrorMessage(), is("error"));
        assertThat(mWizardPage.isPageComplete(), is(false));
        status.setValue(ValidationStatus.ok());
        assertThat(mWizardPage.getErrorMessage(), nullValue());
        assertThat(mWizardPage.isPageComplete(), is(true));
        status.setValue(ValidationStatus.error("error2"));
        assertThat(mWizardPage.getErrorMessage(), is("error2"));
        assertThat(mWizardPage.isPageComplete(), is(false));
    }

    @Test
    @UI
    public void testMultipleErrorsShowsFirstRegularError() {
        final WritableValue status1 = new WritableValue(new RequiredStatus("required"), IStatus.class);
        final MultiValidator validator1 = new MultiValidator() {
            @Override
            protected IStatus validate() {
                return (IStatus) status1.getValue();
            }
        };
        mDataBindingContext.addValidationStatusProvider(validator1);
        final WritableValue status2 = new WritableValue(new RequiredStatus("required2"), IStatus.class);
        final MultiValidator validator2 = new MultiValidator() {
            @Override
            protected IStatus validate() {
                return (IStatus) status2.getValue();
            }
        };
        mDataBindingContext.addValidationStatusProvider(validator2);
        mWizardDialog.open();
        assertThat(mWizardPage.getErrorMessage(), nullValue());
        assertThat(mWizardPage.isPageComplete(), is(false));
        status2.setValue(ValidationStatus.error("error2"));
        assertThat(mWizardPage.getErrorMessage(), is("error2"));
        assertThat(mWizardPage.isPageComplete(), is(false));
        status1.setValue(ValidationStatus.error("error"));
        assertThat(mWizardPage.getErrorMessage(), is("error"));
        assertThat(mWizardPage.isPageComplete(), is(false));
        status1.setValue(ValidationStatus.ok());
        assertThat(mWizardPage.getErrorMessage(), is("error2"));
        assertThat(mWizardPage.isPageComplete(), is(false));
        status2.setValue(ValidationStatus.ok());
        assertThat(mWizardPage.getErrorMessage(), nullValue());
        assertThat(mWizardPage.isPageComplete(), is(true));
    }

    @Test
    @UI
    public void testErrorGoesAwayWhenFixed() {
        final WritableValue status1 = new WritableValue(ValidationStatus.error("error"), IStatus.class);
        final MultiValidator validator1 = new MultiValidator() {
            @Override
            protected IStatus validate() {
                return (IStatus) status1.getValue();
            }
        };
        mDataBindingContext.addValidationStatusProvider(validator1);
        final WritableValue status2 = new WritableValue(new RequiredStatus("required"), IStatus.class);
        final MultiValidator validator2 = new MultiValidator() {
            @Override
            protected IStatus validate() {
                return (IStatus) status2.getValue();
            }
        };
        mDataBindingContext.addValidationStatusProvider(validator2);
        mWizardDialog.open();
        assertThat(mWizardPage.getErrorMessage(), is("error"));
        assertThat(mWizardPage.isPageComplete(), is(false));
        status1.setValue(ValidationStatus.ok());
        assertThat(mWizardPage.getErrorMessage(), nullValue());
        assertThat(mWizardPage.isPageComplete(), is(false));
    }

}
