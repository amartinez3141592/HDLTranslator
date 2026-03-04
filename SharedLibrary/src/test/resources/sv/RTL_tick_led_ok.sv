module tick_light(
	input logic btn_activate,
	input logic VCC,
	input logic GND,
	input logic clk,
	input logic reset,
	output logic red_led
);
	logic next_isActivated;
	logic isActivated;
	typedef enum logic [1:0] {
		S0 = 2'b10,
		S1 = 2'b01
	} state_t;
	state_t next_state;
	state_t state;
	always_ff @(posedge clk or negedge reset) begin
		if (!reset) begin
			isActivated <= 1'b0;
			state <= S0;
		end else begin
			isActivated <= next_isActivated;
			state <= next_state;
		end
	end
	always_comb begin 
		next_state = state;
		next_isActivated = isActivated;
		red_led = 1'b0;
		case(state)
			S0: begin
				red_led=VCC;
				if (VCC) begin next_state = S1;
				end
			end
			S1: begin
				red_led=GND;
				if (VCC) begin next_state = S0;
				end
			end
		endcase
	end

endmodule

