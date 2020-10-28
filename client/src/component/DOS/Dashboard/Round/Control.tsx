import * as React from 'react';

import { Button, Intent, ProgressBar, Card, Elevation } from '@blueprintjs/core';

import fetchReport from 'corla/action/dos/fetchReport';
import startNextRound from 'corla/action/dos/startNextRound';

interface ControlProps {
    canRenderReport: boolean;
    currentRound: number;
}

interface ControlState {
    roundStartSent: boolean;
}

class Control extends React.Component<ControlProps,ControlState>  {
    constructor(props: ControlProps) {
        super(props);
        this.state = { roundStartSent: false };
    }

    
    public render() {
        const { canRenderReport, currentRound } = this.props;

        const waitForNextRound = () => {
            this.setState({ roundStartSent: true });
            startNextRound().then(function (r) {
                // use the result here
                if (r.ok) {
                    console.log('round started');
                }
                this.setState({ roundStartSent: false });
            })
                .catch(function (reason) {
                    console.log("waitForNextRound error in submitAction " + reason);
                    this.setState({ roundStartSent: false });
                });
    
        };

        const ButtonDiv = () => {
            return (
                <div className='state-dashboard-round'>
                    <div>
                        <h4>Round {currentRound} completed</h4>
                        <Button intent={Intent.PRIMARY}
                            onClick={waitForNextRound}>
                            Start round {currentRound + 1}
                        </Button>
                    </div>
                    <div>
                        <Button large
                            disabled={!canRenderReport}
                            intent={Intent.PRIMARY}
                            icon='import'
                            onClick={fetchReport}>
                            Download audit report
                    </Button>
                    </div>
                </div>
            );
        };

        function ProgressDiv() {
            return (
                <Card interactive={false} elevation={Elevation.TWO}>
                    <span style={{display: "inline-block",marginBottom: "20px"}}>
                    Round {currentRound + 1} has been started. Please wait for the operation to complete. It might take a couple minutes. Once complete, page will refresh.
                    </span>
                    <div>
                     <ProgressBar  intent={Intent.SUCCESS}/>
                     </div>
                </Card>
            );
        }


        if (this.state.roundStartSent) {
            return (
                <ProgressDiv />
            )

        } else {
            return (
                <div>
                    <ButtonDiv />
                </div>
            )
        }
    }
};

export default Control;
