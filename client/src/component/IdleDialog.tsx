import * as React from 'react'
import IdleTimer from 'react-idle-timer'
import { IdleTimerProps } from 'react-idle-timer'
import * as Modal from 'react-modal'
import logout from 'corla/action/logout';

const customStyles = {
  content : {
    top                   : '50%',
    left                  : '50%',
    right                 : 'auto',
    bottom                : 'auto',
    marginRight           : '-50%',
    transform             : 'translate(-50%, -50%)'
  }
};
 

export default class IdleDialog extends React.Component {
  idleTimer  : IdleTimer | null
  idleTimer2 : IdleTimer | null
  timeout : number
  timeout2 : number
  state : { 
    remaining: number
    isIdle: boolean
    isIdle2: boolean
    lastActive: Date
    elapsed: number
  }

  constructor(props: IdleTimerProps) {
    super(props)
    this.timeout = 300000  // 300000 - 5 minutes, 60000  - 1 minute 
    this.timeout2 =  360000 //60000 // 60000  - 1 minute 
    this.idleTimer = null
    this.idleTimer2 = null
    this.state = {
      remaining: this.timeout,
      isIdle: false,
      isIdle2: false,
      lastActive: new Date(),
      elapsed: 0
    }
    // Bind event handlers and methods
    this.handleOnActive = this.handleOnActive.bind(this)
    this.handleOnClose = this.handleOnClose.bind(this)
    this.handleOnIdle = this.handleOnIdle.bind(this)
    this.handleReset = this.handleReset.bind(this)
    this.handlePause = this.handlePause.bind(this)
    this.handleResume = this.handleResume.bind(this)
    this.handleOnActive2 = this.handleOnActive2.bind(this)
    this.handleOnIdle2 = this.handleOnIdle2.bind(this)
  }

  componentDidMount() {
    this.setState({
      remaining: this.idleTimer && this.idleTimer.getRemainingTime(),
      lastActive: this.idleTimer && this.idleTimer.getLastActiveTime(),
      elapsed: this.idleTimer && this.idleTimer.getElapsedTime()
    })

    setInterval(() => {
      this.setState({
        remaining: this.idleTimer && this.idleTimer.getRemainingTime(),
        lastActive: this.idleTimer && this.idleTimer.getLastActiveTime(),
        elapsed: this.idleTimer && this.idleTimer.getElapsedTime()
      })
    }, 1000)
  }

  render() {
    return (
      <div>
        <IdleTimer
          ref={ref => { this.idleTimer = ref }}
          onActive={this.handleOnActive}
          onIdle={this.handleOnIdle}
          timeout={this.timeout}
        />
      // Logout if nothing selected  
       <IdleTimer
          ref={ref => { this.idleTimer2 = ref }}
          onActive={this.handleOnActive2}
          onIdle={this.handleOnIdle2}
          timeout={this.timeout2}
        />
        <Modal isOpen={this.state.isIdle}
        style={customStyles} >
        <h2>You've been idle for a while!</h2>
        <p>You will be logged out soon</p>
        <div>
          <button onClick={this.handleLogout}>Log me out</button>
          <button onClick={this.handleOnClose}>Keep me signed in</button>
        </div>
      </Modal>
      </div>
      
    )
  }

 
  handleOnClose() {
    this.setState({ isIdle: false })
    this.handleReset()
  }

  handleOnActive() {
    if(!this.idleTimer)
     this.setState({ isIdle: false })
  }

  handleOnIdle() {
    this.setState({ isIdle: true })
  }

  handleLogout() {
    logout();
   }
 
  handleActive() {
    this.setState({ isIdle: false })
  }

  handleReset() {
    if (this.idleTimer) this.idleTimer.reset()
    if (this.idleTimer2) this.idleTimer2.reset()
  }

  handlePause() {
    if (this.idleTimer) this.idleTimer.pause()
  }

  handleResume() {
    if (this.idleTimer) this.idleTimer.resume()
  }

  // Logout if nothing selected
  handleOnIdle2() {
    this.handleLogout()
  }
  handleOnActive2() {
    if(!this.idleTimer2)
     this.setState({ isIdle2: false })
  }


}
