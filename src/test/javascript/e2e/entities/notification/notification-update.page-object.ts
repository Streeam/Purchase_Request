import { element, by, ElementFinder } from 'protractor';

export default class NotificationUpdatePage {
  pageTitle: ElementFinder = element(by.id('cidApp.notification.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  commentInput: ElementFinder = element(by.css('input#notification-comment'));
  sentDateInput: ElementFinder = element(by.css('input#notification-sentDate'));
  readInput: ElementFinder = element(by.css('input#notification-read'));
  formatSelect: ElementFinder = element(by.css('select#notification-format'));
  companyInput: ElementFinder = element(by.css('input#notification-company'));
  referenced_userInput: ElementFinder = element(by.css('input#notification-referenced_user'));
  employeeSelect: ElementFinder = element(by.css('select#notification-employee'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setCommentInput(comment) {
    await this.commentInput.sendKeys(comment);
  }

  async getCommentInput() {
    return this.commentInput.getAttribute('value');
  }

  async setSentDateInput(sentDate) {
    await this.sentDateInput.sendKeys(sentDate);
  }

  async getSentDateInput() {
    return this.sentDateInput.getAttribute('value');
  }

  getReadInput() {
    return this.readInput;
  }
  async setFormatSelect(format) {
    await this.formatSelect.sendKeys(format);
  }

  async getFormatSelect() {
    return this.formatSelect.element(by.css('option:checked')).getText();
  }

  async formatSelectLastOption() {
    await this.formatSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }
  async setCompanyInput(company) {
    await this.companyInput.sendKeys(company);
  }

  async getCompanyInput() {
    return this.companyInput.getAttribute('value');
  }

  async setReferenced_userInput(referenced_user) {
    await this.referenced_userInput.sendKeys(referenced_user);
  }

  async getReferenced_userInput() {
    return this.referenced_userInput.getAttribute('value');
  }

  async employeeSelectLastOption() {
    await this.employeeSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async employeeSelectOption(option) {
    await this.employeeSelect.sendKeys(option);
  }

  getEmployeeSelect() {
    return this.employeeSelect;
  }

  async getEmployeeSelectedOption() {
    return this.employeeSelect.element(by.css('option:checked')).getText();
  }

  async save() {
    await this.saveButton.click();
  }

  async cancel() {
    await this.cancelButton.click();
  }

  getSaveButton() {
    return this.saveButton;
  }
}
