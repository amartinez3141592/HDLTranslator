module RTL_X(
	input logic PP,
	input logic FF_L,
	input logic VCC,
	input logic GND,
	input logic clk,
	input logic reset,
	output logic Pista,
	output logic [5:0] Posicion,
	output logic [1:0] Comando
);
	logic [5:0] next_pos;
	logic [3:0] next_pst;
	logic next_Flag;
	logic [5:0] pos;
	logic [3:0] pst;
	logic Flag;
	typedef enum logic [3:0] {
		S0 = 4'b1000,
		S1 = 4'b0100,
		S2 = 4'b0010,
		S3 = 4'b0001
	} state_t;
	state_t next_state;
	state_t state;
	always_ff @(posedge clk or negedge reset) begin
		if (!reset) begin
			pos <= 6'b000000;
			pst <= 4'b0000;
			Flag <= 1'b0;
			state <= S0;
		end else begin
			pos <= next_pos;
			pst <= next_pst;
			Flag <= next_Flag;
			state <= next_state;
		end
	end
	always_comb begin 
		next_state = state;
		next_pos = pos;
		next_pst = pst;
		next_Flag = Flag;
		Posicion=pos;
		Pista=pst;
		Flag=!Flag;
		Comando = 2'b00;
		case(state)
			S0: begin
				next_pst={VCC,VCC,VCC,VCC};
				next_pos={VCC,VCC,VCC,VCC,VCC,GND};
				if (VCC) begin next_state = S1;
				end
			end
			S1: begin
				Comando={VCC,VCC};
				if (!PP) begin next_state = S1;
				end else if (PP) begin next_state = S2;
				end
			end
			S2: begin
				if(Flag) begin
					next_pos={pos[4],pos[3],pos[2],pos[1],pos[0],pos[5]};
				end
				Comando={GND,GND};
				if (!PP) begin next_state = S1;
				end else if (PP&&FF_L) begin next_state = S2;
				end else if (PP&&!FF_L) begin next_state = S3;
				end
			end
			S3: begin
				if(Flag) begin
					next_pos={pos[4],pos[3],pos[2],pos[1],pos[0],pos[5]};
				end
				Comando={GND,VCC};
				if (FF_L) begin next_state = S2;
				end else if (!FF_L) begin next_state = S3;
				end
			end
		endcase
	end

endmodule

