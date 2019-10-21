import React from 'react';
import { Button, UncontrolledPopover, PopoverHeader, PopoverBody } from 'reactstrap';
const popoverInfo = props => (
  <div>
    <div style={{ textAlign: 'right' }}>
      <Button style={{ backgroundColor: '#4e5d6c00', borderColor: '#4e5d6c00' }} id="PopoverFocus" type="button">
        <img src={`content/images/Info-icon.png`} style={{ maxHeight: '20px' }} />
      </Button>
    </div>
    <UncontrolledPopover trigger="focus" placement="left" target="PopoverFocus">
      <PopoverHeader>{props.popupTitle}</PopoverHeader>
      <PopoverBody>{props.popupBody}</PopoverBody>
    </UncontrolledPopover>
  </div>
);

export default popoverInfo;
