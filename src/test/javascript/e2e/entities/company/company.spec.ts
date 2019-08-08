/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import CompanyComponentsPage from './company.page-object';
import { CompanyDeleteDialog } from './company.page-object';
import CompanyUpdatePage from './company-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';
import path from 'path';

const expect = chai.expect;

describe('Company e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let companyUpdatePage: CompanyUpdatePage;
  let companyComponentsPage: CompanyComponentsPage;
  /*let companyDeleteDialog: CompanyDeleteDialog;*/
  const fileToUpload = '../../../../../../src/main/webapp/content/images/logo-jhipster.png';
  const absolutePath = path.resolve(__dirname, fileToUpload);

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();
    await waitUntilDisplayed(navBarPage.entityMenu);
  });

  it('should load Companies', async () => {
    await navBarPage.getEntityPage('company');
    companyComponentsPage = new CompanyComponentsPage();
    expect(await companyComponentsPage.getTitle().getText()).to.match(/Companies/);
  });

  it('should load create Company page', async () => {
    await companyComponentsPage.clickOnCreateButton();
    companyUpdatePage = new CompanyUpdatePage();
    expect(await companyUpdatePage.getPageTitle().getAttribute('id')).to.match(/cidApp.company.home.createOrEditLabel/);
    await companyUpdatePage.cancel();
  });

  /* it('should create and save Companies', async () => {
        async function createCompany() {
            await companyComponentsPage.clickOnCreateButton();
            await companyUpdatePage.setNameInput('name');
            expect(await companyUpdatePage.getNameInput()).to.match(/name/);
            await companyUpdatePage.setEmailInput('email');
            expect(await companyUpdatePage.getEmailInput()).to.match(/email/);
            await companyUpdatePage.setPhoneInput('phone');
            expect(await companyUpdatePage.getPhoneInput()).to.match(/phone/);
            await companyUpdatePage.setAddressLine1Input('addressLine1');
            expect(await companyUpdatePage.getAddressLine1Input()).to.match(/addressLine1/);
            await companyUpdatePage.setAddressLine2Input('addressLine2');
            expect(await companyUpdatePage.getAddressLine2Input()).to.match(/addressLine2/);
            await companyUpdatePage.setCityInput('city');
            expect(await companyUpdatePage.getCityInput()).to.match(/city/);
            await companyUpdatePage.setCountryInput('country');
            expect(await companyUpdatePage.getCountryInput()).to.match(/country/);
            await companyUpdatePage.setPostcodeInput('postcode');
            expect(await companyUpdatePage.getPostcodeInput()).to.match(/postcode/);
            await companyUpdatePage.setCompanyLogoInput(absolutePath);
            await waitUntilDisplayed(companyUpdatePage.getSaveButton());
            await companyUpdatePage.save();
            await waitUntilHidden(companyUpdatePage.getSaveButton());
            expect(await companyUpdatePage.getSaveButton().isPresent()).to.be.false;
        }

        await createCompany();
        await companyComponentsPage.waitUntilLoaded();
        const nbButtonsBeforeCreate = await companyComponentsPage.countDeleteButtons();
        await createCompany();

        await companyComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
        expect(await companyComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    });*/

  /* it('should delete last Company', async () => {
        await companyComponentsPage.waitUntilLoaded();
        const nbButtonsBeforeDelete = await companyComponentsPage.countDeleteButtons();
        await companyComponentsPage.clickOnLastDeleteButton();

        const deleteModal = element(by.className('modal'));
        await waitUntilDisplayed(deleteModal);

        companyDeleteDialog = new CompanyDeleteDialog();
        expect(await companyDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/cidApp.company.delete.question/);
        await companyDeleteDialog.clickOnConfirmButton();

        await companyComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
        expect(await companyComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    });*/

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
