import * as React from 'react';

import { connect, MapStateToProps } from 'react-redux';

import action from 'corla/action';

function withSync<P, SelectP, TOwnProps, TState, BindP, BindS>(
    Wrapped: React.ComponentType<P>,
    didMount: string,
    mapStateToProps: MapStateToProps<SelectP, TOwnProps, TState>,
    bind?: Bind<BindP, BindS>,
) {
    type Props = P & SelectP & TOwnProps & BindP;

    class Wrapper extends React.Component<Props> {
        public componentDidMount() {
            action(didMount);
        }

        public render() {
            return <Wrapped { ...this.props } />;
        }
    }

    if (bind) {
        return connect(mapStateToProps, bind)(Wrapper);
    } else {
        return connect(mapStateToProps)(Wrapper);
    }
}

export default withSync;
