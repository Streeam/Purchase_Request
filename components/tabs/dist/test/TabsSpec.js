"use strict";

var _react = _interopRequireDefault(require("react"));

var _enzyme = require("enzyme");

var _Tab = _interopRequireDefault(require("../src/Tab"));

var _Tabs = _interopRequireDefault(require("../src/Tabs"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

describe('<Tabs>', function () {
  it('Should show the correct tab', function () {
    var wrapper = (0, _enzyme.mount)(_react.default.createElement(_Tabs.default, {
      id: "test",
      defaultActiveKey: 1
    }, _react.default.createElement(_Tab.default, {
      title: "Tab 1",
      eventKey: 1
    }, "Tab 1 content"), _react.default.createElement(_Tab.default, {
      title: "Tab 2",
      eventKey: 2
    }, "Tab 2 content")));
    wrapper.assertSingle('TabPane[eventKey=1] .active');
    wrapper.assertSingle('NavLink[eventKey=1] a.active');
  });
  it('Should allow tab to have React components', function () {
    var tabTitle = _react.default.createElement("strong", {
      className: "special-tab"
    }, "Tab 2");

    (0, _enzyme.mount)(_react.default.createElement(_Tabs.default, {
      id: "test",
      defaultActiveKey: 2
    }, _react.default.createElement(_Tab.default, {
      title: "Tab 1",
      eventKey: 1
    }, "Tab 1 content"), _react.default.createElement(_Tab.default, {
      title: tabTitle,
      eventKey: 2
    }, "Tab 2 content"))).assertSingle('NavLink a .special-tab');
  });
  it('Should call onSelect when tab is selected', function (done) {
    function onSelect(key) {
      assert.equal(key, '2');
      done();
    }

    (0, _enzyme.mount)(_react.default.createElement(_Tabs.default, {
      id: "test",
      onSelect: onSelect,
      activeKey: 1
    }, _react.default.createElement(_Tab.default, {
      title: "Tab 1",
      eventKey: "1"
    }, "Tab 1 content"), _react.default.createElement(_Tab.default, {
      title: "Tab 2",
      eventKey: "2"
    }, "Tab 2 content"))).find('NavLink[eventKey="2"] a').simulate('click');
  });
  it('Should have children with the correct DOM properties', function () {
    var wrapper = (0, _enzyme.mount)(_react.default.createElement(_Tabs.default, {
      id: "test",
      defaultActiveKey: 1
    }, _react.default.createElement(_Tab.default, {
      title: "Tab 1",
      className: "custom",
      eventKey: 1
    }, "Tab 1 content"), _react.default.createElement(_Tab.default, {
      title: "Tab 2",
      tabClassName: "tcustom",
      eventKey: 2
    }, "Tab 2 content")));
    wrapper.assertSingle('a.nav-link.tcustom');
    wrapper.assertNone('a.nav-link.custom');
    wrapper.assertSingle('div.tab-pane.custom#test-tabpane-1');
  });
  it('Should pass variant to Nav', function () {
    (0, _enzyme.mount)(_react.default.createElement(_Tabs.default, {
      id: "test",
      variant: "pills",
      defaultActiveKey: 1,
      transition: false
    }, _react.default.createElement(_Tab.default, {
      title: "Tab 1",
      eventKey: 1
    }, "Tab 1 content"), _react.default.createElement(_Tab.default, {
      title: "Tab 2",
      eventKey: 2
    }, "Tab 2 content"))).assertSingle('nav.nav-pills');
  });
  it('Should pass disabled to Nav', function () {
    (0, _enzyme.mount)(_react.default.createElement(_Tabs.default, {
      id: "test",
      defaultActiveKey: 1
    }, _react.default.createElement(_Tab.default, {
      title: "Tab 1",
      eventKey: 1
    }, "Tab 1 content"), _react.default.createElement(_Tab.default, {
      title: "Tab 2",
      eventKey: 2,
      disabled: true
    }, "Tab 2 content"))).assertSingle('a.nav-link.disabled');
  });
});

//# sourceMappingURL=TabsSpec.js.map