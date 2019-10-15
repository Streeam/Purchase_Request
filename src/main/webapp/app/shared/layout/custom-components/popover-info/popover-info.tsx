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
      <PopoverHeader>Company Status</PopoverHeader>
      <PopoverBody>
        The top part of the page shows the company’s details. The bottom part shows all the company’s employee and their roles. The manager
        has the option to edit the company’s details, view every employee’s details and fire them from the company.
      </PopoverBody>
    </UncontrolledPopover>
  </div>
);

export default popoverInfo;
